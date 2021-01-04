package fr.impact.launcherlearn;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import fr.trxyy.alternative.alternative_api.GameEngine;
import fr.trxyy.alternative.alternative_api.account.AccountType;
import fr.trxyy.alternative.alternative_api.auth.GameAuth;
import fr.trxyy.alternative.alternative_api.updater.GameUpdater;
import fr.trxyy.alternative.alternative_api.utils.FontLoader;
import fr.trxyy.alternative.alternative_api.utils.config.LauncherConfig;
import fr.trxyy.alternative.alternative_api_ui.LauncherAlert;
import fr.trxyy.alternative.alternative_api_ui.LauncherPane;
import fr.trxyy.alternative.alternative_api_ui.base.IScreen;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherButton;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherImage;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherLabel;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherPasswordField;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherProgressBar;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherRectangle;
import fr.trxyy.alternative.alternative_api_ui.components.LauncherTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import re.alwyn974.minecraftserverping.MinecraftServerPing;
import re.alwyn974.minecraftserverping.MinecraftServerPingInfos;
import re.alwyn974.minecraftserverping.MinecraftServerPingOptions;

public class LauncherPanel extends IScreen {
	/** LOGIN */
	private LauncherTextField usernameField;
	private LauncherPasswordField passwordField;
	private LauncherButton loginButton;
	private LauncherButton settingsButton;
	/** UPDATE */
	public Timeline timeline;
	private DecimalFormat decimalFormat = new DecimalFormat(".#");
	private Thread updateThread;
	private GameUpdater updater = new GameUpdater();
	private LauncherRectangle updateRectangle;
	private LauncherLabel updateLabel;
	private LauncherLabel currentFileLabel;
	private LauncherLabel percentageLabel;
	private LauncherLabel currentStep;
	private LauncherLabel hypixelStats;
	/** USERNAME SAVER, CONFIG SAVER */
	public LauncherConfig config;
	/** PROGRESS BAR */
	public LauncherProgressBar bar;
	/** GAMEENGINE REQUIRED */
	private GameEngine theGameEngine;
	/** AUTO LOGIN */
	private Timer autoLoginTimer;
	private LauncherLabel autoLoginLabel;
	private LauncherRectangle autoLoginRectangle;
	private LauncherButton autoLoginButton;
	
	// Se souvenir de moi
	private CheckBox rememberMe;

	/** MUSIC */
	private LauncherButton muteMusic;
	private LauncherButton resumeMusic;

	public LauncherPanel(Pane root, GameEngine engine) throws IOException {
		this.theGameEngine = engine;
		/** ===================== CONFIGURATION UTILISATEUR ===================== */
		this.config = new LauncherConfig(engine);
		this.config.loadConfiguration();
		/** ===================== CASE PSEUDONYME ===================== */
		this.usernameField = new LauncherTextField(root);
		this.usernameField.setText((String) this.config.getValue("username"));
		this.usernameField.setPosition(theGameEngine.getWidth() / 2 - 135, theGameEngine.getHeight() / 2 - 57);
		this.usernameField.setSize(270, 50);
		this.usernameField.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
		this.usernameField.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		this.usernameField.setVoidText("Nom de compte");
		/** ===================== CASE MOT DE PASSE ===================== */
		this.passwordField = new LauncherPasswordField(root);
		this.passwordField.setPosition(theGameEngine.getWidth() / 2 - 135, theGameEngine.getHeight() / 2);
		this.passwordField.setSize(270, 50);
		this.passwordField.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
		this.passwordField.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		this.passwordField.setVoidText("Mot de passe (vide = crack)");
		if((boolean) config.getValue("rememberme") == true) 
		{
			passwordField.setText((String) config.getValue("password"));
		} 
		else 
		{
			passwordField.setText("");
		} 
		
		/*// Test Lib Alwyn
		MinecraftServerPingInfos data = new MinecraftServerPing().getPing(new MinecraftServerPingOptions().setHostname("").setPort(25565));
		this.hypixelStats = new LauncherLabel(root);
		this.hypixelStats.setText("Hypixel est en version " + data.getVersion().getName() + " il y a une latence de " + data.getLatency() + " ms avec la France, il y a " + data.getPlayers().getOnline() + " de connectés, son motd est : " + data.getDescription());
		this.hypixelStats.setPosition(20, 50);
		this.hypixelStats.setVisible(true);*/
		
		//
		
		/** ===================== BOUTON DE CONNEXION ===================== */
		this.loginButton = new LauncherButton(root);
		this.loginButton.setText("Se connecter");
		this.loginButton.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 22F));
		this.loginButton.setPosition(theGameEngine.getWidth() / 2 - 67, theGameEngine.getHeight() / 2 + 60);
		this.loginButton.setSize(200, 45);
		this.loginButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		this.loginButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

			
				config.updateValue("username", usernameField.getText());
				if((boolean) config.getValue("rememberme") == true) 
				{
					config.updateValue("password", passwordField.getText());
				} 
				else 
				{
					config.updateValue("password", "");
				} 
				
				/**
				 * ===================== AUTHENTIFICATION OFFLINE (CRACK) =====================
				 */
				if (usernameField.getText().length() < 3) {
					new LauncherAlert("Authentification echouee",
							"Il y a un probleme lors de la tentative de connexion: Le pseudonyme doit comprendre au moins 3 caracteres.");
				} else if (usernameField.getText().length() > 3 && passwordField.getText().isEmpty()) {
					GameAuth auth = new GameAuth(usernameField.getText(), passwordField.getText(), AccountType.OFFLINE);
					if (auth.isLogged()) {
						update(auth);
					}
				}
				/** ===================== AUTHENTIFICATION OFFICIELLE ===================== */
				else if (usernameField.getText().length() > 3 && !passwordField.getText().isEmpty()) {
					GameAuth auth = new GameAuth(usernameField.getText(), passwordField.getText(), AccountType.MOJANG);
					if (auth.isLogged()) {
						/*
						final String imageURI = "https://minotar.net/avatar/" + usernameField.getText() + ".png"; 
						final Image image = new Image(imageURI);
				        final ImageView imageView = new ImageView(image); 
				        imageView.setX(30);
				        imageView.setY(30);
				        imageView.setVisible(true);
				        imageView.setFitWidth(30); 
				        imageView.setFitHeight(30); 
				        root.getChildren().add(imageView);
						*/
						update(auth);
					} else {
						new LauncherAlert("Authentification echouee!",
								"Impossible de se connecter, l'authentification semble etre une authentification 'en-ligne'"
										+ " \nIl y a un probleme lors de la tentative de connexion. \n\n-Verifiez que le pseudonyme comprenne au minimum 3 caracteres. (compte non migrer)"
										+ "\n-Faites bien attention aux majuscules et minuscules. \nAssurez-vous d'utiliser un compte Mojang.");
					}
				} else {
					new LauncherAlert("Authentification echouee!",
							"Impossible de se connecter, l'authentification semble etre une authentification 'hors-ligne'"
									+ " \nIl y a un probleme lors de la tentative de connexion. \n\n-Verifiez que le pseudonyme comprenne au minimum 3 caracteres.");
				}

			}
		});
		
		/** ===================== CHECKBOX SE SOUVENIR ===================== */
		this.rememberMe = new CheckBox();
		this.rememberMe.setText("Se souvenir de moi");
		this.rememberMe.setSelected((boolean) config.getValue("rememberme"));
		this.rememberMe.setOpacity(1.0);
		this.rememberMe.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
		this.rememberMe.setStyle("-fx-text-fill: white;");
		this.rememberMe.setLayoutX(300);
		this.rememberMe.setLayoutY(165);
		this.rememberMe.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				HashMap<String, String> configMap = new HashMap<String, String>();
				configMap.put("rememberme", "" + rememberMe.isSelected());
				config.updateValues(configMap);
			}
		});
		
		root.getChildren().add(rememberMe);
		
		/** ===================== BOUTON PARAMETRES ===================== */
		this.settingsButton = new LauncherButton(root);
		this.settingsButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
		LauncherImage imageButton = new LauncherImage(root,
				getResourceLocation().loadImage(theGameEngine, "settings.png"));
		imageButton.setSize(27, 27);
		this.settingsButton.setGraphic(imageButton);
		this.settingsButton.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
		this.settingsButton.setPosition(theGameEngine.getWidth() / 2 - 135, theGameEngine.getHeight() / 2 + 60);
		this.settingsButton.setSize(60, 45);
		this.settingsButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Scene scene = new Scene(createSettingsPanel());
				Stage stage = new Stage();
				scene.setFill(Color.TRANSPARENT);
				stage.setResizable(false);
				stage.initStyle(StageStyle.TRANSPARENT);
				stage.setTitle("Parametres Launcher");
				stage.setWidth(500);
				stage.setHeight(230);
				stage.setScene(scene);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();
			}
		});

		/** MUSIC **/
			this.muteMusic = new LauncherButton(root);
			this.muteMusic.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
			LauncherImage muteMusicImage = new LauncherImage(root,
					getResourceLocation().loadImage(theGameEngine, "mute.png"));
			imageButton.setSize(27, 27);
			this.muteMusic.setGraphic(muteMusicImage);
			this.muteMusic.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
			this.muteMusic.setPosition(theGameEngine.getWidth() / 2 + 180, theGameEngine.getHeight() / 2 + 60);
			this.muteMusic.setSize(30, 22);
			this.muteMusic.setOnAction(new EventHandler<ActionEvent>() {
	
				@Override
				public void handle(ActionEvent event) {
					muteMusic.setVisible(false);
					resumeMusic.setVisible(true);
					LauncherMain.muteMusic();
				}
			});
			
			this.resumeMusic = new LauncherButton(root);
			this.resumeMusic.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-text-fill: white;");
			LauncherImage resumeMusicImage = new LauncherImage(root,
					getResourceLocation().loadImage(theGameEngine, "resume.png"));
			imageButton.setSize(27, 27);
			this.resumeMusic.setGraphic(resumeMusicImage);
			this.resumeMusic.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
			this.resumeMusic.setPosition(theGameEngine.getWidth() / 2 + 180, theGameEngine.getHeight() / 2 + 60);
			this.resumeMusic.setSize(30, 22);
			this.resumeMusic.setVisible(false);
			this.resumeMusic.setOnAction(new EventHandler<ActionEvent>() {
	
				@Override
				public void handle(ActionEvent event) {
					resumeMusic.setVisible(false);
					muteMusic.setVisible(true);
					LauncherMain.resumeMusic();
				}
			});
		/**
		 * ============================== MISE A JOUR ==============================
		 **/
		this.updateRectangle = new LauncherRectangle(root, theGameEngine.getWidth() / 2 - 175,
				theGameEngine.getHeight() / 2 - 60, 350, 180);
		this.updateRectangle.setArcWidth(10.0);
		this.updateRectangle.setArcHeight(10.0);
		this.updateRectangle.setFill(Color.rgb(0, 0, 0, 0.60));
		this.updateRectangle.setVisible(false);
		/** =============== LABEL TITRE MISE A JOUR =============== **/
		this.updateLabel = new LauncherLabel(root);
		this.updateLabel.setText("- MISE A JOUR -");
		this.updateLabel.setFont(Font.font("FontName", FontWeight.BOLD, 10.0f));
		this.updateLabel.setAlignment(Pos.CENTER);
		this.updateLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 22F));
		this.updateLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.updateLabel.setPosition(theGameEngine.getWidth() / 2 - 95, theGameEngine.getHeight() / 2 - 55);
		this.updateLabel.setOpacity(1);
		this.updateLabel.setSize(190, 40);
		this.updateLabel.setVisible(false);
		/** =============== ETAPE DE MISE A JOUR =============== **/
		this.currentStep = new LauncherLabel(root);
		this.currentStep.setText("Preparation de la mise a jour.");
		this.currentStep.setFont(Font.font("Verdana", FontPosture.ITALIC, 18F)); // FontPosture.ITALIC
		this.currentStep.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.currentStep.setAlignment(Pos.CENTER);
		this.currentStep.setPosition(theGameEngine.getWidth() / 2 - 160, theGameEngine.getHeight() / 2 + 83);
		this.currentStep.setOpacity(0.4);
		this.currentStep.setSize(320, 40);
		this.currentStep.setVisible(false);
		/** =============== FICHIER ACTUEL EN TELECHARGEMENT =============== **/
		this.currentFileLabel = new LauncherLabel(root);
		this.currentFileLabel.setText("launchwrapper-12.0.jar");
		this.currentFileLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 18F));
		this.currentFileLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.currentFileLabel.setAlignment(Pos.CENTER);
		this.currentFileLabel.setPosition(theGameEngine.getWidth() / 2 - 160, theGameEngine.getHeight() / 2 + 25);
		this.currentFileLabel.setOpacity(0.8);
		this.currentFileLabel.setSize(320, 40);
		this.currentFileLabel.setVisible(false);
		/** =============== POURCENTAGE =============== **/
		this.percentageLabel = new LauncherLabel(root);
		this.percentageLabel.setText("0%");
		this.percentageLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 30F));
		this.percentageLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
		this.percentageLabel.setAlignment(Pos.CENTER);
		this.percentageLabel.setPosition(theGameEngine.getWidth() / 2 - 50, theGameEngine.getHeight() / 2 - 5);
		this.percentageLabel.setOpacity(0.8);
		this.percentageLabel.setSize(100, 40);
		this.percentageLabel.setVisible(false);

		this.bar = new LauncherProgressBar(root);
		this.bar.setPosition(theGameEngine.getWidth() / 2 - 125, theGameEngine.getHeight() / 2 + 60);
		this.bar.setSize(250, 20);
		this.bar.setVisible(false);

		/** =============== LOGIN AUTOMATIQUE (CRACK SEULEMENT) =============== **/
		this.autoLoginRectangle = new LauncherRectangle(root, 0, theGameEngine.getHeight() - 32, 1000,
				theGameEngine.getHeight());
		this.autoLoginRectangle.setFill(Color.rgb(0, 0, 0, 0.70));
		this.autoLoginRectangle.setOpacity(1.0);
		this.autoLoginRectangle.setVisible(false);

		this.autoLoginLabel = new LauncherLabel(root);
		this.autoLoginLabel.setText("Connexion auto dans 3 secondes. Appuyez sur ECHAP pour annuler.");
		this.autoLoginLabel.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 18F));
		this.autoLoginLabel.setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
		this.autoLoginLabel.setPosition(theGameEngine.getWidth() / 2 - 280, theGameEngine.getHeight() - 34);
		this.autoLoginLabel.setOpacity(0.7);
		this.autoLoginLabel.setSize(700, 40);
		this.autoLoginLabel.setVisible(false);

		this.autoLoginButton = new LauncherButton(root);
		this.autoLoginButton.setText("Annuler");
		this.autoLoginButton.setFont(FontLoader.loadFont("Comfortaa-Regular.ttf", "Comfortaa", 14F));
		this.autoLoginButton.setPosition(theGameEngine.getWidth() / 2 + 60, theGameEngine.getHeight() - 30);
		this.autoLoginButton.setSize(200, 20);
		this.autoLoginButton.setStyle("-fx-background-color: rgba(255, 255, 255, 0.4); -fx-text-fill: black;");
		this.autoLoginButton.setVisible(false);
		this.autoLoginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				autoLoginTimer.cancel();
				autoLoginLabel.setVisible(false);
				autoLoginButton.setVisible(false);
				autoLoginRectangle.setVisible(false);
			}
		});
		String userName = (String) this.config.getValue("username");
		if (userName.length() > 2 && !userName.contains("@")
				&& (boolean) this.config.getValue("autologin").equals(true)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					autoLoginTimer = new Timer();
					TimerTask timerTask = new TimerTask() {
						int waitTime = 5;
						int elapsed = 0;

						@Override
						public void run() {
							elapsed++;

							if (elapsed % waitTime == 0) {
								loginButton.fire();
								autoLoginTimer.cancel();
								autoLoginLabel.setVisible(false);
								autoLoginButton.setVisible(false);
								autoLoginRectangle.setVisible(false);
							} else {
								int time = (waitTime - (elapsed % waitTime));
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										autoLoginLabel.setText("Connexion auto dans " + time + " secondes.");
									}
								});
							}
						}
					};
					autoLoginTimer.schedule(timerTask, 0, 1000);
					autoLoginLabel.setVisible(true);
					autoLoginRectangle.setVisible(true);
					autoLoginButton.setVisible(true);
				}
			});
		}

	}

	private void update(GameAuth auth) {
		this.usernameField.setDisable(true);
		this.passwordField.setDisable(true);
		this.loginButton.setDisable(true);
		this.settingsButton.setDisable(true);
		this.usernameField.setVisible(false);
		this.passwordField.setVisible(false);
		this.loginButton.setVisible(false);
		this.settingsButton.setVisible(false);
		this.updateRectangle.setVisible(true);
		this.updateLabel.setVisible(true);
		this.currentStep.setVisible(true);
		this.currentFileLabel.setVisible(true);
		this.percentageLabel.setVisible(true);
		this.bar.setVisible(true);
		theGameEngine.getGameLinks().JSON_URL = theGameEngine.getGameLinks().BASE_URL + this.config.getValue("version") + ".json";
		updater.reg(theGameEngine);
		updater.reg(auth.getSession());
		theGameEngine.reg(this.updater);

		this.updateThread = new Thread() {
			public void run() {
				theGameEngine.getGameUpdater().run();
			}
		};
		this.updateThread.start();
		/**
		 * ===================== REFAICHIR LE NOM DU FICHIER, PROGRESSBAR, POURCENTAGE
		 * =====================
		 **/
		this.timeline = new Timeline(
				new KeyFrame[] { new KeyFrame(javafx.util.Duration.seconds(0.0D), new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						timelineUpdate(theGameEngine);
					}
				}, new javafx.animation.KeyValue[0]),
						new KeyFrame(javafx.util.Duration.seconds(0.1D), new javafx.animation.KeyValue[0]) });
		this.timeline.setCycleCount(Animation.INDEFINITE);
		this.timeline.play();
	}

	protected Parent createSettingsPanel() {
		LauncherPane contentPane = new LauncherPane(theGameEngine);
		Rectangle rect = new Rectangle(500, 230);
		rect.setArcHeight(15.0);
		rect.setArcWidth(15.0);
		contentPane.setClip(rect);
		contentPane.setStyle("-fx-background-color: transparent;");
		new LauncherSettings(contentPane, theGameEngine, this);
		return contentPane;
	}

	public void timelineUpdate(GameEngine engine) {
		if (engine.getGameUpdater().downloadedFiles > 0) {
			this.percentageLabel.setText(decimalFormat.format(
					engine.getGameUpdater().downloadedFiles * 100.0D / engine.getGameUpdater().filesToDownload) + "%");
		}
		this.currentFileLabel.setText(engine.getGameUpdater().getCurrentFile());
		this.currentStep.setText(engine.getGameUpdater().getCurrentInfo());
		double percent = (engine.getGameUpdater().downloadedFiles * 100.0D / engine.getGameUpdater().filesToDownload
				/ 100.0D);
		this.bar.setProgress(percent);
	}

	public LauncherTextField getUsernameField() {
		return usernameField;
	}

	public LauncherPasswordField getPasswordField() {
		return passwordField;
	}

}