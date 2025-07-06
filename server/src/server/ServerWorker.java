package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServerWorker handles individual client examination sessions.
 * Each client gets a dedicated worker thread to manage their examination.
 * 
 * @author RADHA
 */
public class ServerWorker extends Thread {
    
    private static final Logger LOGGER = Logger.getLogger(ServerWorker.class.getName());
    private static final int TOTAL_QUESTIONS = 10;

    private final Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private final ArrayList<Question> questionSet;
    private final Database database;
    private final int studentId;

    
    /**
     * Creates a new ServerWorker for handling a client's examination session.
     * 
     * @param clientSocket the connected client socket
     * @param database the database instance
     * @param studentId the authenticated student's ID
     * @throws ClassNotFoundException if database driver is not found
     * @throws SQLException if database operations fail
     */
    public ServerWorker(Socket clientSocket, Database database, int studentId) throws ClassNotFoundException, SQLException {
        this.clientSocket = clientSocket;
        this.database = database;
        this.studentId = studentId;
        this.questionSet = database.fetch();
        
        LOGGER.info("ServerWorker created for student ID: " + studentId + 
                   " with " + questionSet.size() + " questions");
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting examination session for student ID: " + studentId);
            handleExamination();
            LOGGER.info("Examination session completed for student ID: " + studentId);
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO error during examination for student " + studentId + ": " + e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during examination for student " + studentId + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Examination interrupted for student " + studentId + ": " + e.getMessage(), e);
            Thread.currentThread().interrupt(); // Restore interrupt status
        } finally {
            closeResources();
        }
    }

    
    /**
     * Handles the examination process for the connected client.
     * Sends questions and receives answers, calculates marks.
     * 
     * @throws IOException if communication with client fails
     * @throws SQLException if database operations fail
     * @throws InterruptedException if thread is interrupted
     */
    private void handleExamination() throws IOException, SQLException, InterruptedException {
        int marks = 0;
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        int questionCount = 0;
        int totalQuestions = Math.min(TOTAL_QUESTIONS, questionSet.size());
        
        LOGGER.info("Starting examination with " + totalQuestions + " questions for student ID: " + studentId);
        
        while (questionCount < totalQuestions) {
            if (questionCount >= questionSet.size()) {
                LOGGER.warning("Not enough questions available. Expected: " + totalQuestions + 
                             ", Available: " + questionSet.size());
                break;
            }
            
            // Send question and options to client
            Question currentQuestion = questionSet.get(questionCount);
            String questionData = formatQuestionData(currentQuestion);
            
            out.writeUTF(questionData);
            out.flush();
            
            LOGGER.fine("Sent question " + (questionCount + 1) + " to student ID: " + studentId);
            
            // Receive answer from client
            String clientAnswer = in.readUTF();
            
            // Validate and score the answer
            if (isAnswerCorrect(clientAnswer, currentQuestion.getAns())) {
                marks++;
                LOGGER.fine("Student " + studentId + " answered question " + (questionCount + 1) + " correctly");
            } else {
                LOGGER.fine("Student " + studentId + " answered question " + (questionCount + 1) + " incorrectly. " +
                          "Expected: " + currentQuestion.getAns() + ", Got: " + clientAnswer);
            }
            
            questionCount++;
        }
        
        // Send examination completion signal and final marks
        out.writeUTF("over");
        out.flush();
        out.writeUTF(Integer.toString(marks));
        out.flush();
        
        // Save marks to database
        database.setMarks(marks, studentId);
        
        LOGGER.info("Examination completed for student ID: " + studentId + 
                   ". Final score: " + marks + "/" + questionCount);
    }
    
    /**
     * Formats question data for transmission to client.
     * 
     * @param question the question to format
     * @return formatted question string
     */
    private String formatQuestionData(Question question) {
        return question.getQues() + "|" + 
               question.opt1 + "|" + 
               question.opt2 + "|" + 
               question.opt3 + "|" + 
               question.opt4;
    }
    
    /**
     * Checks if the client's answer is correct.
     * 
     * @param clientAnswer the answer provided by client
     * @param correctAnswer the correct answer
     * @return true if answer is correct, false otherwise
     */
    private boolean isAnswerCorrect(String clientAnswer, String correctAnswer) {
        if (clientAnswer == null || correctAnswer == null) {
            return false;
        }
        return clientAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
    
    /**
     * Safely closes all resources associated with this worker.
     */
    private void closeResources() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing input stream for student " + studentId + ": " + e.getMessage(), e);
        }
        
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing output stream for student " + studentId + ": " + e.getMessage(), e);
        }
        
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing client socket for student " + studentId + ": " + e.getMessage(), e);
        }
        
        LOGGER.info("Resources closed for student ID: " + studentId);
    }
}
