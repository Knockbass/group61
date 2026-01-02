package nl.saxion.game.yourgamename.quest_logic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import nl.saxion.game.yourgamename.entities.Player;
import nl.saxion.game.yourgamename.systems.StatSystem;
import nl.saxion.gameapp.GameApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudyQuizSystem {
    public enum QuizPhase {
        IDLE,
        START,
        MEMORIZE,
        INPUT,
        CHECKING,
        RESULT,
        FINAL_RESULTS
    }

    private QuizPhase currentPhase = QuizPhase.IDLE;
    private Player player;
    private String currentQuestion;
    private String playerInput = "";
    private float timer = 0f;
    private float memorizeTime = 0f;
    private float inputTimeLimit = 30f; // 30 seconds to type
    private boolean isCorrect = false;
    private Random random = new Random();
    
    // Quiz with 4 questions
    private List<String> quizQuestions = new ArrayList<>(); // 4 questions for current quiz
    private int currentQuestionIndex = 0; // 0-3 for 4 questions
    private int correctAnswers = 0; // Track correct answers in current quiz
    private int lastQuizDay = 0; // Track last day quiz was completed
    
    // HUD Camera for UI rendering
    private OrthographicCamera hudCamera;
    
    // Study questions/strings to memorize (pool to select from)
    private List<String> studyQuestions = new ArrayList<>();
    
    public StudyQuizSystem(Player player) {
        this.player = player;
        initializeQuestions();
    }
    
    private void initializeQuestions() {
        studyQuestions.add("ALGORITHM");
        studyQuestions.add("DATA STRUCTURE");
        studyQuestions.add("OBJECT ORIENTED");
        studyQuestions.add("SOFTWARE ENGINEERING");
        studyQuestions.add("DATABASE MANAGEMENT");
        studyQuestions.add("NETWORK PROTOCOL");
        studyQuestions.add("MACHINE LEARNING");
        studyQuestions.add("COMPUTER SCIENCE");
        studyQuestions.add("PROGRAMMING LANGUAGE");
        studyQuestions.add("ARTIFICIAL INTELLIGENCE");
    }
    
    public void startQuiz() {
        if (currentPhase != QuizPhase.IDLE) {
            return; // Already in a quiz
        }
        
        // Check if quiz was already done today
        int currentDay = player.accessStatSystem().getCurrentDay();
        if (lastQuizDay == currentDay) {
            System.out.println("Quiz already completed today! Come back tomorrow.");
            return; // Quiz already done today
        }
        
        currentPhase = QuizPhase.START;
        timer = 0f;
        playerInput = "";
        isCorrect = false;
        currentQuestionIndex = 0;
        correctAnswers = 0;
        quizQuestions.clear();
        
        // Select 4 random questions for this quiz (no duplicates)
        List<String> availableQuestions = new ArrayList<>(studyQuestions);
        for (int i = 0; i < 4 && !availableQuestions.isEmpty(); i++) {
            int randomIndex = random.nextInt(availableQuestions.size());
            quizQuestions.add(availableQuestions.remove(randomIndex));
        }
        
        // Set first question
        currentQuestion = quizQuestions.get(0);
        
        // Random memorize time between 3-6 seconds
        memorizeTime = 3f + random.nextFloat() * 3f;
    }
    
    public void update(float delta) {
        if (currentPhase == QuizPhase.IDLE) {
            return;
        }
        
        timer += delta;
        
        
        switch (currentPhase) {
            case START:
                if (timer >= 1f) { // Show "QUIZ" title for 1 second
                    currentPhase = QuizPhase.MEMORIZE;
                    timer = 0f;
                }
                break;
                
            case MEMORIZE:
                if (timer >= memorizeTime) {
                    currentPhase = QuizPhase.INPUT;
                    timer = 0f;
                }
                break;
                
            case INPUT:
                handleInput();
                if (timer >= inputTimeLimit) {
                    // Time's up - check answer
                    checkAnswer();
                }
                break;
                
            case CHECKING:
                // Transition phase, handled immediately in checkAnswer
                break;
                
            case RESULT:
                if (timer >= 4f) { // Show result for 4 seconds (increased visibility)
                    // Apply results for this question
                    applyQuestionResults();
                    
                    // Move to next question or finish quiz
                    currentQuestionIndex++;
                    if (currentQuestionIndex < 4) {
                        // Move to next question
                        currentQuestion = quizQuestions.get(currentQuestionIndex);
                        playerInput = "";
                        isCorrect = false;
                        timer = 0f;
                        memorizeTime = 3f + random.nextFloat() * 3f;
                        currentPhase = QuizPhase.START; // Start next question
                    } else {
                        // Quiz complete - apply final results and show final screen
                        applyFinalResults();
                        currentPhase = QuizPhase.FINAL_RESULTS;
                        timer = 0f;
                        lastQuizDay = player.accessStatSystem().getCurrentDay();
                        System.out.println("Quiz completed! Score: " + correctAnswers + "/4");
                    }
                }
                break;
                
            case FINAL_RESULTS:
                if (timer >= 6f) { // Show final results for 6 seconds
                    currentPhase = QuizPhase.IDLE;
                }
                break;
                
            case IDLE:
                // Do nothing
                break;
        }
    }
    
    private void handleInput() {
        // Handle backspace
        if (GameApp.isKeyJustPressed(Input.Keys.BACKSPACE) && playerInput.length() > 0) {
            playerInput = playerInput.substring(0, playerInput.length() - 1);
        }
        
        // Handle Enter to submit
        if (GameApp.isKeyJustPressed(Input.Keys.ENTER)) {
            checkAnswer();
            return;
        }
        
        // Handle character input (A-Z, space)
        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (GameApp.isKeyJustPressed(i)) {
                char c = (char) ('A' + (i - Input.Keys.A));
                playerInput += c;
            }
        }
        
        // Handle space
        if (GameApp.isKeyJustPressed(Input.Keys.SPACE)) {
            playerInput += " ";
        }
    }
    
    private void checkAnswer() {
        currentPhase = QuizPhase.CHECKING;
        
        // Normalize both strings (trim, uppercase)
        String normalizedQuestion = currentQuestion.trim().toUpperCase();
        String normalizedInput = playerInput.trim().toUpperCase();
        
        // Check for exact match or allow small mistakes (1-2 character difference)
        if (normalizedInput.equals(normalizedQuestion)) {
            isCorrect = true;
        } else {
            // Allow small mistakes - check if strings are similar (within 2 character difference)
            int difference = Math.abs(normalizedQuestion.length() - normalizedInput.length());
            if (difference <= 2 && normalizedInput.length() > normalizedQuestion.length() * 0.7f) {
                // Check character similarity
                int matches = 0;
                int minLength = Math.min(normalizedQuestion.length(), normalizedInput.length());
                for (int i = 0; i < minLength; i++) {
                    if (normalizedQuestion.charAt(i) == normalizedInput.charAt(i)) {
                        matches++;
                    }
                }
                // If 80% of characters match, consider it correct
                if (matches >= minLength * 0.8f) {
                    isCorrect = true;
                }
            }
        }
        
        currentPhase = QuizPhase.RESULT;
        timer = 0f;
    }
    
    private void applyQuestionResults() {
        // Track correct answers (don't apply stats yet)
        if (isCorrect) {
            correctAnswers++;
        }
    }
    
    // Store final results for display
    private int totalKnowledgeGain = 0;
    private int totalMentalHealthGain = 0;
    private int totalEnergyLoss = 0;
    
    private void applyFinalResults() {
        StatSystem stats = player.accessStatSystem();
        
        // Apply stats based on total correct answers (0-4)
        // Each correct answer: +15 knowledge, +5 mental health, -10 energy
        // Each wrong answer: -10 mental health, -5 energy
        int wrongAnswers = 4 - correctAnswers;
        
        totalKnowledgeGain = correctAnswers * 15;
        totalMentalHealthGain = (correctAnswers * 5) - (wrongAnswers * 10);
        totalEnergyLoss = (correctAnswers * 10) + (wrongAnswers * 5);
        
        stats.getKnowledgeStat().setCurrentValue(
            stats.getKnowledgeStat().get() + totalKnowledgeGain
        );
        stats.getMentalHealthStat().setCurrentValue(
            stats.getMentalHealthStat().get() + totalMentalHealthGain
        );
        stats.getEnergyStat().setCurrentValue(
            stats.getEnergyStat().get() - totalEnergyLoss
        );
        stats.getEnergyStat().applyMaxValueBound();
        stats.getMentalHealthStat().applyMaxValueBound();
    }
    
    public void render(float screenWidth, float screenHeight) {
        if (currentPhase == QuizPhase.IDLE) {
            System.out.println("Quiz render called but phase is IDLE");
            return;
        }
        
        System.out.println("Quiz render - Phase: " + currentPhase + ", Screen params: " + screenWidth + "x" + screenHeight);
        
        // Get actual screen dimensions in pixels for pixel-perfect rendering
        float actualScreenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
        float actualScreenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();
        
        System.out.println("Actual screen: " + actualScreenWidth + "x" + actualScreenHeight);
        
        // Create or update fixed HUD camera using actual screen pixels
        if (hudCamera == null) {
            hudCamera = new OrthographicCamera();
            hudCamera.setToOrtho(false, actualScreenWidth, actualScreenHeight);
        }
        
        // Update HUD camera to match actual screen size
        hudCamera.viewportWidth = actualScreenWidth;
        hudCamera.viewportHeight = actualScreenHeight;
        hudCamera.update();
        
        // Save the current projection matrix
        SpriteBatch batch = GameApp.getSpriteBatch();
        com.badlogic.gdx.math.Matrix4 oldProjection = batch.getProjectionMatrix().cpy();
        
        // Switch to HUD camera for crisp text rendering
        batch.setProjectionMatrix(hudCamera.combined);
        
        GameApp.startSpriteRendering();
        
        float centerX = actualScreenWidth / 2f;
        float topY = actualScreenHeight - 80f;
        float boxY = actualScreenHeight / 2f;
        float inputY = 200f;
        
        // Draw "STUDY SESSION" title at top center with larger spacing
        GameApp.drawTextCentered("hud", "STUDY SESSION", centerX, topY, "blue-600");
        
        switch (currentPhase) {
            case START:
                // Show "QUIZ" title only, then transition to MEMORIZE
                break;
                
            case MEMORIZE:
                // Timer on the right side of title (larger, more visible)
                float timeLeft = memorizeTime - timer;
                int minutes = (int)(timeLeft / 60);
                int seconds = (int)(timeLeft % 60);
                String timerText = String.format("%d:%02d", minutes, seconds);
                String timerColor = timeLeft < 2f ? "red-600" : (timeLeft < 4f ? "orange-500" : "blue-600");
                GameApp.drawText("hud", timerText, actualScreenWidth - 120, topY, timerColor);
                
                // Show question number prominently (e.g., "Question 1/4")
                GameApp.drawTextCentered("hud", "Question " + (currentQuestionIndex + 1) + " of 4", centerX, topY - 60, "purple-600");
                
                // "Remember the text:" instruction
                GameApp.drawTextCentered("default", "Memorize this text:", centerX, topY - 100, "gray-700");
                
                // Draw the question text in the center (larger, more prominent)
                GameApp.drawTextCentered("hud", currentQuestion, centerX, boxY, "black");
                
                // Progress indicator
                float progressBarY = topY - 130f;
                float progress = (currentQuestionIndex + 1) / 4f;
                // Draw progress bar background (simple text-based)
                GameApp.drawText("default", "Progress: [" + "█".repeat((int)(progress * 20)) + "░".repeat(20 - (int)(progress * 20)) + "]", 
                    centerX - 150, progressBarY, "gray-500");
                break;
                
            case INPUT:
                // Timer on the right side of title (larger, more visible)
                float inputTimeLeft = inputTimeLimit - timer;
                int inputMinutes = (int)(inputTimeLeft / 60);
                int inputSeconds = (int)(inputTimeLeft % 60);
                String inputTimerText = String.format("%d:%02d", inputMinutes, inputSeconds);
                String inputTimerColor = inputTimeLeft < 5f ? "red-600" : (inputTimeLeft < 10f ? "orange-500" : "blue-600");
                GameApp.drawText("hud", inputTimerText, actualScreenWidth - 120, topY, inputTimerColor);
                
                // Show question number prominently
                GameApp.drawTextCentered("hud", "Question " + (currentQuestionIndex + 1) + " of 4", centerX, topY - 60, "purple-600");
                
                // "Type your answer:" instruction
                GameApp.drawTextCentered("default", "Type your answer:", centerX, inputY + 60, "gray-700");
                
                // Draw input field background indicator
                GameApp.drawTextCentered("default", "─────────────────────────", centerX, inputY - 10, "gray-400");
                
                // Draw input text with cursor (larger, more visible)
                String displayInput = playerInput.isEmpty() ? "_" : playerInput + "_";
                GameApp.drawTextCentered("hud", displayInput, centerX, inputY, "black");
                
                // Instructions
                GameApp.drawTextCentered("default", "Press ENTER to submit", centerX, inputY - 50, "gray-500");
                break;
                
            case CHECKING:
                // Brief checking phase - show processing with animation
                float checkY = boxY + 20f;
                GameApp.drawTextCentered("hud", "Checking your answer...", centerX, checkY, "blue-600");
                // Animated dots
                int dotCount = ((int)(timer * 2)) % 4;
                String dots = ".".repeat(dotCount);
                GameApp.drawTextCentered("default", dots, centerX, checkY - 30, "gray-500");
                break;
                
            case RESULT:
                // Large, prominent result display
                float resultY = boxY + 80f;
                
                if (isCorrect) {
                    // CORRECT - Large green text
                    GameApp.drawTextCentered("hud", "✓ CORRECT!", centerX, resultY, "green-600");
                    GameApp.drawTextCentered("default", "Well done!", centerX, resultY - 40, "green-500");
                } else {
                    // INCORRECT - Large red text
                    GameApp.drawTextCentered("hud", "✗ INCORRECT", centerX, resultY, "red-600");
                    GameApp.drawTextCentered("default", "The correct answer was:", centerX, resultY - 40, "gray-600");
                    GameApp.drawTextCentered("hud", currentQuestion, centerX, resultY - 70, "blue-600");
                }
                
                // Current score (prominent)
                float scoreY = boxY;
                GameApp.drawTextCentered("hud", "Current Score", centerX, scoreY, "purple-600");
                String scoreText = correctAnswers + " / " + (currentQuestionIndex + 1);
                GameApp.drawTextCentered("hud", scoreText, centerX, scoreY - 40, "black");
                
                // Progress indicator
                float progressBarY2 = scoreY - 80f;
                float progress2 = (currentQuestionIndex + 1) / 4f;
                GameApp.drawText("default", "Progress: [" + "█".repeat((int)(progress2 * 20)) + "░".repeat(20 - (int)(progress2 * 20)) + "]", 
                    centerX - 150, progressBarY2, "gray-500");
                
                // Show next question countdown or completion
                float countdownY = scoreY - 120f;
                if (currentQuestionIndex < 3) {
                    int secondsLeft = 4 - (int)timer;
                    GameApp.drawTextCentered("default", "Next question in " + secondsLeft + " seconds...", 
                        centerX, countdownY, "gray-500");
                } else {
                    GameApp.drawTextCentered("default", "Calculating final results...", 
                        centerX, countdownY, "blue-600");
                }
                break;
                
            case FINAL_RESULTS:
                // Final results screen - very prominent
                float finalY = actualScreenHeight / 2f + 150f;
                
                // Large title
                GameApp.drawTextCentered("hud", "QUIZ COMPLETE!", centerX, finalY, "blue-700");
                
                // Final score (very large and prominent)
                float scoreFinalY = finalY - 80f;
                GameApp.drawTextCentered("hud", "Final Score", centerX, scoreFinalY, "purple-600");
                String finalScoreText = correctAnswers + " / 4";
                String scoreColor = correctAnswers >= 3 ? "green-600" : (correctAnswers >= 2 ? "yellow-600" : "red-600");
                GameApp.drawTextCentered("hud", finalScoreText, centerX, scoreFinalY - 50, scoreColor);
                
                // Performance message
                float perfY = scoreFinalY - 120f;
                String perfMessage = "";
                String perfColor = "";
                if (correctAnswers == 4) {
                    perfMessage = "PERFECT! Excellent work!";
                    perfColor = "green-600";
                } else if (correctAnswers == 3) {
                    perfMessage = "Great job!";
                    perfColor = "green-500";
                } else if (correctAnswers == 2) {
                    perfMessage = "Good effort!";
                    perfColor = "yellow-600";
                } else if (correctAnswers == 1) {
                    perfMessage = "Keep practicing!";
                    perfColor = "orange-500";
                } else {
                    perfMessage = "Don't give up!";
                    perfColor = "red-500";
                }
                GameApp.drawTextCentered("hud", perfMessage, centerX, perfY, perfColor);
                
                // Stat changes (very visible)
                float statsY = perfY - 100f;
                GameApp.drawTextCentered("hud", "Stat Changes:", centerX, statsY, "black");
                
                float statLineY = statsY - 50f;
                // Knowledge
                if (totalKnowledgeGain > 0) {
                    GameApp.drawTextCentered("default", "Knowledge: +" + totalKnowledgeGain, centerX, statLineY, "green-600");
                    statLineY -= 35f;
                }
                // Mental Health
                if (totalMentalHealthGain != 0) {
                    String mhColor = totalMentalHealthGain > 0 ? "green-600" : "red-600";
                    String mhSign = totalMentalHealthGain > 0 ? "+" : "";
                    GameApp.drawTextCentered("default", "Mental Health: " + mhSign + totalMentalHealthGain, centerX, statLineY, mhColor);
                    statLineY -= 35f;
                }
                // Energy
                if (totalEnergyLoss > 0) {
                    GameApp.drawTextCentered("default", "Energy: -" + totalEnergyLoss, centerX, statLineY, "orange-600");
                    statLineY -= 35f;
                }
                
                // Return message
                int secondsLeft = 6 - (int)timer;
                GameApp.drawTextCentered("default", "Returning to game in " + secondsLeft + " seconds...", 
                    centerX, statLineY - 40, "gray-500");
                break;
                
            case IDLE:
                // Do nothing
                break;
        }
        
        GameApp.endSpriteRendering();
        
        // Restore the original projection matrix
        batch.setProjectionMatrix(oldProjection);
    }
    
    public boolean isActive() {
        return currentPhase != QuizPhase.IDLE;
    }
    
    public QuizPhase getCurrentPhase() {
        return currentPhase;
    }
    
    public boolean canStartQuiz() {
        if (currentPhase != QuizPhase.IDLE) {
            return false; // Already in a quiz
        }
        int currentDay = player.accessStatSystem().getCurrentDay();
        return lastQuizDay != currentDay; // Can start if not done today
    }
    
    public void cancel() {
        currentPhase = QuizPhase.IDLE;
        playerInput = "";
        timer = 0f;
        currentQuestionIndex = 0;
        correctAnswers = 0;
        quizQuestions.clear();
    }

    public void setPlayer(Player player){
        this.player = player;
    }
}

