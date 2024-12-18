package com.example.oops_app;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class SelfImprovementApp extends Application {

    // Shared timetable data structure for persistence
    private final String[][] timeTable = new String[9][8];

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Self-Improvement App");

        // Initialize the timetable with default values
        initializeTimeTable();

        // Main Menu UI
        VBox mainMenu = new VBox(10);
        mainMenu.setPadding(new Insets(20));
        mainMenu.setStyle("-fx-alignment: center; -fx-spacing: 10;");

        Label titleLabel = new Label("Welcome to the Self-Improvement App!");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Button calorieCounterButton = new Button("Calorie Counter");
        Button brainTrainerButton = new Button("Brain Trainer");
        Button healthCalculatorButton = new Button("Health Calculator");
        Button sleepTrackerButton = new Button("Sleep Tracker");
        Button waterReminderButton = new Button("Water Reminder");
        Button workoutPlannerButton = new Button("Workout Planner");
        Button dailyPlannerButton = new Button("Daily Planner");
        Button exitButton = new Button("Exit");

        mainMenu.getChildren().addAll(
                titleLabel,
                calorieCounterButton,
                brainTrainerButton,
                healthCalculatorButton,
                sleepTrackerButton,
                waterReminderButton,
                workoutPlannerButton,
                dailyPlannerButton,
                exitButton
        );

        // Main Scene
        Scene mainScene = new Scene(mainMenu, 400, 500);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        // Set up feature navigation
        calorieCounterButton.setOnAction(e -> showCalorieCounter(primaryStage, mainScene));
        brainTrainerButton.setOnAction(e -> showBrainTrainer(primaryStage, mainScene));
        healthCalculatorButton.setOnAction(e -> showHealthCalculator(primaryStage, mainScene));
        sleepTrackerButton.setOnAction(e -> showSleepTracker(primaryStage, mainScene));
        waterReminderButton.setOnAction(e -> showWaterReminder(primaryStage, mainScene));
        workoutPlannerButton.setOnAction(e -> showWorkoutPlanner(primaryStage, mainScene));
        dailyPlannerButton.setOnAction(e -> showDailyPlanner(primaryStage, mainScene));
        exitButton.setOnAction(e -> primaryStage.close());
    }

    private void initializeTimeTable() {
        int time = 6;
        for (int i = 0; i < 9; i++) {
            timeTable[i][0] = String.format("%02d:00 - %02d:00", time, time + 2); // Time slot
            for (int j = 1; j < 8; j++) {
                timeTable[i][j] = "   -"; // Empty activity
            }
            time += 2;
        }
    }

    private void showDailyPlanner(Stage stage, Scene mainScene) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Daily Planner");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // TableView for the timetable
        TableView<String[]> table = new TableView<>();
        table.setEditable(true);

        // Define columns for each day
        String[] days = {"Time Slot", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int col = 0; col < days.length; col++) {
            TableColumn<String[], String> column = new TableColumn<>(days[col]);
            final int colIndex = col;
            column.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue()[colIndex]));

            // Make days editable (excluding the time column)
            if (col > 0) {
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                column.setOnEditCommit(event -> {
                    String[] row = event.getRowValue();
                    row[colIndex] = event.getNewValue();
                });
            }
            table.getColumns().add(column);
        }

        // Populate the table from the shared timetable
        table.getItems().clear();
        for (String[] row : timeTable) {
            table.getItems().add(row);
        }

        // Buttons
        Button resetButton = new Button("Reset All");
        resetButton.setOnAction(e -> {
            initializeTimeTable();
            table.getItems().clear();
            for (String[] row : timeTable) {
                table.getItems().add(row);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(mainScene));

        HBox buttonBox = new HBox(10, resetButton, backButton);
        buttonBox.setStyle("-fx-alignment: center;");

        layout.getChildren().addAll(titleLabel, table, buttonBox);

        stage.setScene(new Scene(layout, 800, 600));
    }

    private final Map<String, Double> foodDatabase = new HashMap<>();
    private double totalCalories = 0.0;

    private void showCalorieCounter(Stage stage, Scene mainScene) {
        // Initialize food database if it's empty
        if (foodDatabase.isEmpty()) {
            foodDatabase.put("bread", 2.5); // Calories per gram
            foodDatabase.put("pasta", 1.31);
            foodDatabase.put("chicken", 2.39);
            foodDatabase.put("milk", 0.62); // Calories per ml
            foodDatabase.put("rice", 1.3);
            foodDatabase.put("egg", 1.55);
            foodDatabase.put("apple", 0.52);
            foodDatabase.put("banana", 0.89);
            foodDatabase.put("potato", 0.77);
            foodDatabase.put("carrot", 0.41);
        }

        // Layout for the calorie counter
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Calorie Counter");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // TextField for food input
        TextField foodField = new TextField();
        foodField.setPromptText("Enter food item (e.g., 'apple')");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter quantity (grams/ml)");

        Button addButton = new Button("Add Calories");
        Label feedbackLabel = new Label();

        // ListView for food log
        ListView<String> foodLog = new ListView<>();
        foodLog.getItems().add("Total Calories: " + totalCalories);

        addButton.setOnAction(e -> {
            String food = foodField.getText().toLowerCase().trim();
            String quantityText = quantityField.getText().trim();

            if (food.isEmpty() || quantityText.isEmpty()) {
                feedbackLabel.setText("Please fill in both fields.");
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityText);

                if (foodDatabase.containsKey(food)) {
                    double caloriesPerUnit = foodDatabase.get(food);
                    double addedCalories = caloriesPerUnit * quantity;
                    totalCalories += addedCalories;
                    foodLog.getItems().add(food + " (" + quantity + " units): " + addedCalories + " kcal");
                } else {
                    feedbackLabel.setText("Food not found. Adding manual calories.");
                    foodLog.getItems().add(food + " (Unknown quantity): " + quantity + " kcal");
                    totalCalories += quantity; // Add manual calories
                }

                foodLog.getItems().set(0, "Total Calories: " + totalCalories);
                feedbackLabel.setText("Calories added successfully!");
                foodField.clear();
                quantityField.clear();
            } catch (NumberFormatException ex) {
                feedbackLabel.setText("Quantity must be a number.");
            }
        });

        // Back button to return to the main menu
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(mainScene));

        layout.getChildren().addAll(
                titleLabel, foodField, quantityField, addButton, foodLog, feedbackLabel, backButton
        );
        stage.setScene(new Scene(layout, 400, 500));
    }


    private void showBrainTrainer(Stage stage, Scene mainScene) {
        // Layout for Brain Trainer
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Brain Trainer");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Options for games
        Button equationGameButton = new Button("Equation Generator and Checker");
        Button memoryGameButton = new Button("Memory Game");
        Button backButton = new Button("Back");

        Label resultLabel = new Label();

        // Game buttons
        equationGameButton.setOnAction(e -> playEquationGame(stage, mainScene));
        memoryGameButton.setOnAction(e -> playMemoryGame(stage, mainScene));

        // Back button to return to the Main Menu
        backButton.setOnAction(e -> stage.setScene(mainScene));

        layout.getChildren().addAll(titleLabel, equationGameButton, memoryGameButton, resultLabel, backButton);

        stage.setScene(new Scene(layout, 400, 600));
    }

    // Updated Equation Game Method
    private void playEquationGame(Stage stage, Scene mainScene) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label instructions = new Label("Solve equations to test your math skills!");
        TextField numEquationsField = new TextField();
        numEquationsField.setPromptText("Enter number of equations to solve");

        Button startButton = new Button("Start Game");
        Button backButton = new Button("Back");

        ListView<String> equationList = new ListView<>();
        Random random = new Random();

        startButton.setOnAction(e -> {
            try {
                int numEquations = Integer.parseInt(numEquationsField.getText());
                equationList.getItems().clear();

                int correctCount = 0;

                for (int i = 0; i < numEquations; i++) {
                    int num1 = random.nextInt(20) + 1;
                    int num2 = random.nextInt(20) + 1;
                    char operation = generateRandomOperation(random);
                    int correctAnswer = calculateAnswer(num1, num2, operation);

                    TextInputDialog inputDialog = new TextInputDialog();
                    inputDialog.setHeaderText(String.format("Solve: %d %c %d", num1, operation, num2));
                    inputDialog.setContentText("Your answer:");

                    Optional<String> input = inputDialog.showAndWait();
                    if (input.isPresent()) {
                        int userAnswer = Integer.parseInt(input.get());
                        if (userAnswer == correctAnswer) {
                            equationList.getItems().add(String.format("Correct: %d %c %d = %d", num1, operation, num2, userAnswer));
                            correctCount++;
                        } else {
                            equationList.getItems().add(String.format("Wrong: %d %c %d (Correct: %d)", num1, operation, num2, correctAnswer));
                        }
                    }
                }

                instructions.setText(String.format("You solved %d out of %d correctly!", correctCount, numEquations));
            } catch (NumberFormatException ex) {
                instructions.setText("Invalid input. Please enter a number.");
            }
        });

        // Back button returns to the Brain Trainer main menu
        backButton.setOnAction(e -> showBrainTrainer(stage, mainScene));

        layout.getChildren().addAll(instructions, numEquationsField, startButton, equationList, backButton);
        stage.setScene(new Scene(layout, 400, 600));
    }

    private int calculateAnswer(int num1, int num2, char operation) {
        switch (operation) {
            case '+': return num1 + num2;
            case '-': return num1 - num2;
            case '*': return num1 * num2;
            case '/': return num2 != 0 ? num1 / num2 : 0;
            default: throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }

    private char generateRandomOperation(Random random) {
        char[] operations = {'+', '-', '*', '/'};
        return operations[random.nextInt(operations.length)];
    }

    // Updated Memory Game Method
    private void playMemoryGame(Stage stage, Scene mainScene) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label instructions = new Label("Test your memory skills!");
        TextField numCountField = new TextField();
        numCountField.setPromptText("Enter number of numbers to memorize");

        Button startButton = new Button("Start Game");
        Button backButton = new Button("Back");

        Random random = new Random();

        startButton.setOnAction(e -> {
            try {
                int numCount = Integer.parseInt(numCountField.getText());
                int[] numbers = new int[numCount];

                StringBuilder numberSequence = new StringBuilder();
                for (int i = 0; i < numCount; i++) {
                    numbers[i] = random.nextInt(100);
                    numberSequence.append(numbers[i]).append(" ");
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Memorize the numbers");
                alert.setContentText(numberSequence.toString());
                alert.showAndWait();

                int correctCount = 0;

                for (int i = 0; i < numCount; i++) {
                    TextInputDialog inputDialog = new TextInputDialog();
                    inputDialog.setHeaderText(String.format("Enter number %d:", i + 1));
                    Optional<String> input = inputDialog.showAndWait();

                    if (input.isPresent() && Integer.parseInt(input.get()) == numbers[i]) {
                        correctCount++;
                    }
                }

                instructions.setText(String.format("You remembered %d out of %d numbers!", correctCount, numCount));
            } catch (NumberFormatException ex) {
                instructions.setText("Invalid input. Please enter a valid number.");
            }
        });

        // Back button returns to the Brain Trainer main menu
        backButton.setOnAction(e -> showBrainTrainer(stage, mainScene));

        layout.getChildren().addAll(instructions, numCountField, startButton, backButton);
        stage.setScene(new Scene(layout, 400, 600));
    }


    private void showHealthCalculator(Stage stage, Scene mainScene) {
        // Layout for the Health Calculator
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Health Calculator");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextField weightField = new TextField();
        weightField.setPromptText("Enter your weight in kg");

        TextField heightField = new TextField();
        heightField.setPromptText("Enter your height in cm");

        TextField ageField = new TextField();
        ageField.setPromptText("Enter your age in years");

        TextField genderField = new TextField();
        genderField.setPromptText("Enter your gender (M or F)");

        Button calculateButton = new Button("Calculate");
        Button backButton = new Button("Back");

        Label resultLabel = new Label();

        calculateButton.setOnAction(e -> {
            try {
                // Get user inputs
                double weight = Double.parseDouble(weightField.getText());
                double height = Double.parseDouble(heightField.getText());
                int age = Integer.parseInt(ageField.getText());
                char gender = genderField.getText().toUpperCase().charAt(0);

                // Validate gender input
                if (gender != 'M' && gender != 'F') {
                    resultLabel.setText("Invalid gender entered! Use M or F.");
                    return;
                }

                // Calculate BMI
                double heightInMeters = height / 100.0;
                double bmi = weight / (heightInMeters * heightInMeters);

                // Calculate BMR
                double bmr = (gender == 'M')
                        ? (10 * weight) + (6.25 * height) - (5 * age) + 5
                        : (10 * weight) + (6.25 * height) - (5 * age) - 161;

                // Display results
                resultLabel.setText(String.format("Your BMI: %.2f\nYour BMR: %.2f calories/day", bmi, bmr));
            } catch (NumberFormatException ex) {
                resultLabel.setText("Invalid input! Please enter valid numbers.");
            }
        });

        // Back button returns to the Main Menu
        backButton.setOnAction(e -> stage.setScene(mainScene));

        layout.getChildren().addAll(titleLabel, weightField, heightField, ageField, genderField, calculateButton, resultLabel, backButton);

        stage.setScene(new Scene(layout, 400, 600));
    }


    private void showSleepTracker(Stage stage, Scene mainScene) {
        // Layout for Sleep Tracker
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Sleep Tracker");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextField sleepGoalField = new TextField();
        sleepGoalField.setPromptText("Enter your daily sleep goal in hours");

        TextField hoursSleptField = new TextField();
        hoursSleptField.setPromptText("Enter hours you have slept so far");

        Button trackButton = new Button("Track Sleep");
        Button backButton = new Button("Back");

        Label resultLabel = new Label();

        trackButton.setOnAction(e -> {
            try {
                // Get user input
                double sleepGoal = Double.parseDouble(sleepGoalField.getText());
                double hoursSlept = Double.parseDouble(hoursSleptField.getText());
                double remainingSleep = sleepGoal - hoursSlept;

                if (remainingSleep > 0) {
                    resultLabel.setText("You need to sleep " + remainingSleep + " more hours to meet your daily goal.");
                } else {
                    resultLabel.setText("Great job! You've already met your daily sleep goal.");
                }

                // Keep tracking additional sleep hours until the goal is met
                while (remainingSleep > 0) {
                    // Asking for additional sleep hours
                    TextInputDialog inputDialog = new TextInputDialog();
                    inputDialog.setTitle("Additional Sleep");
                    inputDialog.setHeaderText("Enter additional sleep hours.");
                    Optional<String> result = inputDialog.showAndWait();

                    if (result.isPresent()) {
                        double additionalSleep = Double.parseDouble(result.get());
                        hoursSlept += additionalSleep;
                        remainingSleep = sleepGoal - hoursSlept;

                        if (remainingSleep > 0) {
                            resultLabel.setText("You still need to sleep " + remainingSleep + " more hours.");
                        } else {
                            resultLabel.setText("Congratulations! You've met your daily sleep goal.");
                            break;  // End the loop once the goal is reached
                        }
                    }
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter valid numbers for hours.");
            }
        });

        // Back button returns to the Main Menu
        backButton.setOnAction(e -> stage.setScene(mainScene));

        layout.getChildren().addAll(titleLabel, sleepGoalField, hoursSleptField, trackButton, resultLabel, backButton);

        stage.setScene(new Scene(layout, 400, 600));
    }


    private void showWaterReminder(Stage stage, Scene mainScene) {
        // Layout for Water Reminder
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Water Reminder");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextField waterGoalField = new TextField();
        waterGoalField.setPromptText("Enter your daily water goal in liters");

        TextField reminderIntervalField = new TextField();
        reminderIntervalField.setPromptText("Reminder Interval (in minutes)");

        Button startButton = new Button("Start Water Reminder");
        Button backButton = new Button("Back");

        Label resultLabel = new Label();

        startButton.setOnAction(e -> {
            try {
                // Get user input
                double waterGoal = Double.parseDouble(waterGoalField.getText());
                int reminderInterval = Integer.parseInt(reminderIntervalField.getText());

                if (waterGoal <= 0 || reminderInterval <= 0) {
                    resultLabel.setText("Please enter valid positive values.");
                    return;
                }

                // Initialize variables
                double waterDrank = 0;

                // Create a timer to send reminders at the specified interval
                Timer timer = new Timer();
                double finalWaterDrank = waterDrank;
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (finalWaterDrank < waterGoal) {
                            resultLabel.setText("\nReminder: Stay hydrated! You've drunk "
                                    + finalWaterDrank + " liters out of your " + waterGoal + " liter goal.");
                        } else {
                            resultLabel.setText("\nCongratulations! You've reached your daily water goal.");
                            timer.cancel();
                        }
                    }
                }, 0, reminderInterval * 60 * 1000); // Set the reminder interval

                // While loop to keep track of water intake
                while (waterDrank < waterGoal) {
                    TextInputDialog inputDialog = new TextInputDialog();
                    inputDialog.setTitle("Water Intake");
                    inputDialog.setHeaderText("Enter the amount of water you just drank (in liters).");
                    Optional<String> result = inputDialog.showAndWait();

                    if (result.isPresent()) {
                        double waterInput = Double.parseDouble(result.get());

                        if (waterInput <= 0) {
                            resultLabel.setText("Please enter a valid positive amount of water.");
                            continue; // Skip to the next iteration for invalid input
                        }

                        waterDrank += waterInput;

                        if (waterDrank >= waterGoal) {
                            resultLabel.setText("Great job! You've achieved your daily water goal!");
                            resultLabel.setText(String.format("You've exceeded your goal by %.2f liters.", waterDrank - waterGoal));
                            timer.cancel();
                            break;
                        } else {
                            resultLabel.setText(String.format("You've now drunk %.2f liters out of your %.2f liter goal.", waterDrank, waterGoal));
                        }
                    }
                }

            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter valid numbers for both fields.");
            }
        });

        // Back button returns to the Main Menu
        backButton.setOnAction(e -> stage.setScene(mainScene));

        layout.getChildren().addAll(titleLabel, waterGoalField, reminderIntervalField, startButton, resultLabel, backButton);

        stage.setScene(new Scene(layout, 400, 600));
    }


    private void showWorkoutPlanner(Stage stage, Scene mainScene) {
        // Layout for Workout Planner
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label titleLabel = new Label("Workout Planner");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Input fields for height and weight
        TextField heightField = new TextField();
        heightField.setPromptText("Enter your height in cm");

        TextField weightField = new TextField();
        weightField.setPromptText("Enter your weight in kg");

        Label bmiLabel = new Label();
        Label goalLabel = new Label();
        Label programLabel = new Label();

        ListView<String> workoutList = new ListView<>();

        Button calculateButton = new Button("Calculate BMI & Plan Workout");
        calculateButton.setOnAction(e -> {
            try {
                // Parse input
                double heightCm = Double.parseDouble(heightField.getText());
                double weightKg = Double.parseDouble(weightField.getText());

                // Calculate BMI
                double bmi = calculateBMI(heightCm, weightKg);
                bmiLabel.setText(String.format("Your BMI: %.2f", bmi));

                // Determine fitness goal
                String fitnessGoal = determineFitnessGoal(bmi);
                goalLabel.setText("Your Fitness Goal: " + fitnessGoal);

                // Suggest a workout program
                workoutList.getItems().clear();
                workoutList.getItems().addAll(suggestWorkoutProgram(fitnessGoal));

                programLabel.setText("Workout Plan for: " + fitnessGoal);

            } catch (NumberFormatException ex) {
                bmiLabel.setText("Error: Please enter valid numbers for height and weight.");
            }
        });

        // Back button to return to the main menu
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.setScene(mainScene));

        layout.getChildren().addAll(
                titleLabel,
                heightField, weightField, calculateButton,
                bmiLabel, goalLabel, programLabel, workoutList,
                backButton
        );

        stage.setScene(new Scene(layout, 400, 600));
    }

    // Method to calculate BMI
    private double calculateBMI(double heightCm, double weightKg) {
        double heightM = heightCm / 100; // Convert height to meters
        return weightKg / (heightM * heightM);
    }

    // Method to determine the fitness goal based on BMI
    private String determineFitnessGoal(double bmi) {
        if (bmi < 18.5) {
            return "Gaining Muscle";
        } else if (bmi >= 25) {
            return "Losing Weight";
        } else {
            return "Maintaining Fitness";
        }
    }

    // Method to suggest a workout program based on the fitness goal
    private List<String> suggestWorkoutProgram(String fitnessGoal) {
        String[] workouts;

        if ("Gaining Muscle".equals(fitnessGoal)) {
            workouts = new String[] {
                    "Chest Press: 3 x 12",
                    "Leg Press: 3 x 12",
                    "Hack Squats: 3 x 12",
                    "Shoulder Press: 3 x 12",
                    "Bicep Curl: 3 x 12",
                    "Tricep Pushdown: 3 x 12",
                    "Lat Pulldown: 3 x 12",
                    "Seated Row: 3 x 12",
                    "Hamstring Curl: 3 x 12",
                    "Calf Raise: 3 x 12",
                    "Ab Machine: 3 x 15"
            };
        } else if ("Losing Weight".equals(fitnessGoal)) {
            workouts = new String[] {
                    "Treadmill: 3 x 10 min",
                    "Elliptical: 3 x 10 min",
                    "Cycling: 3 x 15 min",
                    "Rowing Machine: 3 x 10 min",
                    "Stair Climber: 3 x 10 min",
                    "Jump Rope: 3 x 2 min",
                    "HIIT Circuit: 3 x 15 min",
                    "Speed Walking: 3 x 15 min",
                    "Aerobics: 3 x 20 min",
                    "Swimming: 3 x 15 min"
            };
        } else { // Maintaining Fitness
            workouts = new String[] {
                    "Push-ups: 3 x 15",
                    "Squats: 3 x 15",
                    "Planks: 3 x 30 seconds",
                    "Lunges: 3 x 12 per leg",
                    "Jumping Jacks: 3 x 20",
                    "Mountain Climbers: 3 x 20",
                    "Burpees: 3 x 10",
                    "Dumbbell Rows: 3 x 12",
                    "Russian Twists: 3 x 20",
                    "Bicycle Crunches: 3 x 20"
            };
        }

        return Arrays.asList(workouts);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
