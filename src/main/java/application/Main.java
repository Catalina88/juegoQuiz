package application;
	
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Label;
import javafx.application.Platform;

// Se extiende para usar JavaFX por Application
public class Main extends Application {
	
	private String question;
	private String goodAnswer;
	private String[] options;
	private List<Question> questions = new ArrayList<>();
	private javafx.scene.control.Label questionLabel, scoreLabel;
	private Button option1, option2, option3, option4, restartButton;
	private int score = 0;
	private Timeline timer = new Timeline();
	private String playerName;

	private void pedirNombreJugador() {
	    TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("TRIVICOON");
	    dialog.setHeaderText("🌟 Bienvenido a TRIVICOON 🌟 \nIngresa tu nombre de usuario:");
	    dialog.setContentText("Nombre:");

		dialog.getDialogPane().getStylesheets().add(
				getClass().getResource("application.css").toExternalForm()
		);


		dialog.showAndWait().ifPresent(name -> {
	        playerName = name.trim();
	        if (playerName.isEmpty()) {
	            showAlert("Error", "El nombre no puede estar vacío. Inténtalo de nuevo.", AlertType.ERROR);
	            pedirNombreJugador(); // Volver a preguntar si el nombre está vacío
	        }
	    });
	}
	@Override
	public void start(Stage primaryStage) {
		pedirNombreJugador();
		definirPuntosParaGanar();
		cargarPreguntas(); //Cargar lista de preguntas
		seleccionarPreguntaAleatoria(); //Elegir una pregunta aleatoria
		iniciarTemporizador();
		
		
		restartButton = new Button("Reiniciar");
		restartButton.setOnAction(e -> reiniciarJuego());
		restartButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		
		BorderPane root = new BorderPane();
		
		Label titleLabel= new Label("TRIVICOON");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
		
			questionLabel = new javafx.scene.control.Label(question);
			questionLabel.setStyle("-fx-font-size: 20px;");
			// Creamos los botones para mostrar las opciones en la que una de ellas es correccta
			option1 = new Button(options[0]);  // Se le Asigna la opción 0 a option 1
			option2 = new Button(options[1]);  // Se le asigna la opción 1 a option 2
			option3 = new Button(options[2]);  // Se le asigna la opción 2 a option 3
			option4 = new Button(options[3]);  // Se le asigna la opción 3 a option 4

			//Verificación para que al hacer click muestre si es correcta o no
			option1.setOnAction(e -> checkAnswer(option1.getText()));
			option2.setOnAction(e -> checkAnswer(option2.getText()));
			option3.setOnAction(e -> checkAnswer(option3.getText()));
			option4.setOnAction(e -> checkAnswer(option4.getText()));
			
			//Organiza los botones de las opciones en orden vertical
 
			VBox vbox = new VBox(10);  // Un espaciado entre cada botón
			vbox.setAlignment(Pos.CENTER);
			vbox.getChildren().addAll(option1, option2, option3, option4, restartButton);
			
			//Se organiza la pregunta en la parte central de la ventana
			
			VBox topBox = new VBox(10);
			topBox.setAlignment(Pos.CENTER);
			topBox.getChildren().addAll(titleLabel, questionLabel);
			root.setTop(topBox);
			BorderPane.setAlignment(topBox, Pos.CENTER);

			// Colocar las opciones de respuesta en el centro
			root.setCenter(vbox);
			BorderPane.setAlignment(vbox, Pos.CENTER);

			scoreLabel = new javafx.scene.control.Label("Puntaje: 0");
			scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
			root.setBottom(scoreLabel);
			BorderPane.setAlignment(scoreLabel, Pos.CENTER);
			
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
		} 
	
	private void iniciarTemporizador() {
	    if (timer != null) { 
	    	timer.stop(); // Detener el anterior si existía
	    }
	
	    timer = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
	        showAlert("¡Se agotó el tiempo!", "No respondiste a tiempo. Pasa a la siguiente pregunta.", AlertType.WARNING);
	        score--; // Penalización - 1 punto
	        scoreLabel.setText("Puntaje: " + score);
	        seleccionarPreguntaAleatoria();
	        actualizarPantalla();
	        iniciarTemporizador();
	    }));
	    timer.setCycleCount(1);
	    timer.play();
	}

	private int puntosParaGanar;

	private void definirPuntosParaGanar() {
	    TextInputDialog dialog = new TextInputDialog("5");
	    dialog.setTitle("Configurar Juego");
	    dialog.setHeaderText("Selecciona cuántos puntos necesitas para ganar (1-10):");
	    dialog.setContentText("Puntos:");

	    dialog.showAndWait().ifPresent(puntos -> {
	        try {
	            int valor = Integer.parseInt(puntos);
	            if (valor >= 1 && valor <= 10) {
	                puntosParaGanar = valor;
	            } else {
	                showAlert("Error", "Por favor ingresa un número entre 1 y 10.", AlertType.ERROR);
	                definirPuntosParaGanar(); // Volver a preguntar
	            }
	        } catch (NumberFormatException e) {
	            showAlert("Error", "Por favor ingresa un número válido.", AlertType.ERROR);
	            definirPuntosParaGanar();
	        }
	    });
	}
	
	private void reiniciarJuego() {
		definirPuntosParaGanar(); // Pedir puntaje para ganar
		timer.stop(); // Detener el temporizador antes de reiniciar
	    score = 0;
	    scoreLabel.setText("Puntaje: 0"); // Restablecer el puntaje en pantalla
	    seleccionarPreguntaAleatoria(); // Seleccionar nueva pregunta
	    actualizarPantalla(); // Actualizar la pantalla con la nueva pregunta
	    iniciarTemporizador(); // Reiniciar el temporizador
	
	}
		
	private void cargarPreguntas() {
		questions.add(new Question("¿Cómo se llama el papa actual?", "León", new String[]{"Benito", "Francisco", "León", "Pablo"}));
		questions.add(new Question("¿Cuál es la capital de Colombia?", "Bogotá", new String[]{"Medellín", "Cali", "Bogotá", "Santa Marta"}));
		questions.add(new Question("¿Cuál es el nombre del río más grande del mundo?", "Nilo", new String[]{"Nilo", "Amazonas", "Río Amarillo", "Río Congo"}));
		questions.add(new Question("¿Cuál es el nombre de la moneda usada en Estados Unidos?", "Dolar", new String[]{"Euro", "Dolar", "Peso", "Yeng"}));
		questions.add(new Question("¿Cúal es un plato típico de Colombia?", "Ajiaco", new String[]{"Taco al pastor", "Hamburguesa con queso", "Pasta Bolognesa", "Ajiaco"}));
		questions.add(new Question("¿Cuándo empezó la primera guerra mundial?", "1914", new String[]{"1924", "1914", "1910", "1912"}));
		questions.add(new Question("¿En qué país nació Adolf Hitler?", "Austria", new String[]{"Rusia", "Alemania", "Austria", "Italia"}));
		questions.add(new Question("¿En qué año se hundió el Titanic?", "1912", new String[]{"1908", "1916", "1910", "1912"}));
		questions.add(new Question("¿Cuál es la ciudad de los rascacielos?", "Nueva York", new String[]{"Toronto", "Florida", "Nueva York", "Tokio"}));
		questions.add(new Question("¿Cómo se llama el proceso por el cual las plantas se alimentan?", "Fotosíntesis", new String[]{"Metamorfosis", "Antesis", "Fotosíntesis", "Polinización"}));	
		
	}
	
	private void seleccionarPreguntaAleatoria() {
		if (questions.isEmpty()) {
			throw new RuntimeException ("La lista de preguntas está vacía.");
		}
		
		Random random = new Random();
		Question q = questions.get(random.nextInt(questions.size())); // Selección aleatoria

	    this.question = q.getQuestion();
	    this.goodAnswer = q.getCorrectAnswer();
	    this.options = q.getOptions();
	}
	
	private void checkAnswer(String selectedAnswer) {
		if (selectedAnswer.equals(goodAnswer)) {
			score++; // Aumenta el puntaje si la respuesta es correcta
			showAlert("¡Correcto!", "¡La respuesta es correcta!", AlertType.INFORMATION);
		} else {
			score--; // Disminuye el puntaje si la respuesta es incorrecta
			if (score <0) score = 0;
			showAlert("Incorrecto", "La respuesta es incorrecta. Intenta de nuevo.", AlertType.ERROR);
		}
		//Actualizar puntaje en pantalla
		scoreLabel.setText("Puntaje: "+ score);
		
		if (score >= puntosParaGanar) {
			if (timer != null) {
			timer.stop(); // Detener el temporizador
			}
			showAlert("¡Felicidades " + playerName + "!", "Has ganado con " + String.valueOf(score) + " puntos.", AlertType.INFORMATION);
			reiniciarJuego(); //Reiniciar el juego
			return;
					
		}
		//Seleccionar una nueva pregunta después de responder
		seleccionarPreguntaAleatoria();
		actualizarPantalla();
	}
	
		private void actualizarPantalla() {
			questionLabel.setText(question); // Cambiar la pregunta en pantalla
			option1.setText(options[0]);
			option2.setText(options[1]);
			option3.setText(options[2]);
			option4.setText(options[3]);
		}

	// Se usa un método para que muestre un mensaje de alerta de si es correcto o incorrecto F

		private void showAlert(String title, String message, AlertType type) {
		    Platform.runLater(() -> {
		        Alert alert = new Alert(type);
		        alert.setTitle(title);
		        alert.setHeaderText(null);
		        alert.setContentText(message);
		        alert.showAndWait(); // Se ejecuta en el hilo correcto
		    });
		}
	
	//Método principal para JavaFX
    
	public static void main(String[] args) {
		launch(args);
	}
	
	class Question {
		private String question;
		private String correctAnswer;
		private String [] options;
		
		public Question(String question, String correctAnswer, String[] options) {
			if (options == null || options.length == 0) {
				throw new IllegalArgumentException ("Las opciones de respuesta no pueden estar vacías.");
			}
			this.question = question;
			this.correctAnswer = correctAnswer;
			this.options = options;
		}
		
		public String getQuestion() { return question; }
		public String getCorrectAnswer() { return correctAnswer; }
		public String[] getOptions() {return options; }
	}
	
	
}

