package Main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.regex.Pattern;

import static javafx.scene.layout.HBox.setHgrow;
import static javafx.scene.layout.VBox.setVgrow;


/**
 * Created by GianDavid on 30.04.2017.
 */
public class DisplayController {
    @FXML
    JFXTabPane tabPane;
    @FXML
    Tab plusTab;

    private String primary = "#00bcd4", primaryLight = "#62efff", primaryDark = "#008ba3";
    private String secondary = "#00bcd4", secondaryLight = "#62efff", secondaryDark = "#008ba3";

    public abstract class SwitchableChangeListener<T> implements ChangeListener<T> {

        private int numInvoked = 0;
        private boolean state = true;

        public void incrNumInvoked() {
            numInvoked++;
        }

        public int getNumInvoked(){
            return numInvoked;
        }


        public void turnOff() {
            state = false;
        }

        public void turnOn() {
            state = true;
        }

        public boolean getState() {
            return state;
        }
    }

    private final SwitchableChangeListener<? super Tab> newTabListener = new SwitchableChangeListener<Tab>() {
        @Override
        public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
            //This works, don't change it. It took hours to get this right, don't change it. I know, this is bad, but please don't change it.

            //System.out.println("Obervable: " + observable + "\noldValue: " + oldValue + "\nnewValue: " + newValue);
            //System.out.println("NumINvoked: " + getNumInvoked() + " After Modulation with 4: " + getNumInvoked()%4 + "\n");
            if (newValue == plusTab && getState()) {
                newTabListener.turnOff();
                incrNumInvoked();
                if (getNumInvoked()%2 == 1)
                    newTab();
                tabPane.getSelectionModel().selectPrevious();
                newTabListener.turnOn();
            }
        }
    };

    @FXML
    public void initialize() {
        tabPane.getStyleClass().add("primary-light-background");
        newTab();
        //For Debugging Tab Selection:
        //System.out.println("First Tab: " + tab);
        //System.out.println("Plus Tab:  " + plusTab +"\n");
        tabPane.getSelectionModel().selectedItemProperty().addListener(newTabListener);
    }

    public void handleURLTextFieldHandleKeyTyped(KeyEvent e, JFXTextField textField, WebEngine engine) {
        if (e.getCode() == KeyCode.ENTER) {
            String in = textField.getText();
            String out = "";
            Boolean google = false;
            if (Pattern.matches("\\bhttp:\\/\\/.+\\..+\\..+", in) || Pattern.matches("\\bhttps:\\/\\/.+\\..+\\..+", in))
                out = in;
            else if (Pattern.matches("www\\..+\\..+", in))
                out = "http://" + in;
            else if (Pattern.matches("\\b.+\\..+", in))
                out = "http://www." + in;
            else
                google = true;
            if (!google)
                engine.load(out);
            else
                engine.load(convertToGoogleSearch(in));
            e.consume();
        }

    }

    public void handleSearchInput(KeyEvent e, JFXTextField searchTextField, WebEngine engine) {
        if (e.getCode() == KeyCode.ENTER) {
            engine.load(convertToGoogleSearch(searchTextField.getText()));
            e.consume();
        }
    }

    private void newTab(){
        SingleSelectionModel<Tab> selection = tabPane.getSelectionModel();
        ObservableList<Tab> tabs = tabPane.getTabs();

        JFXButton backButton = new JFXButton(" "), forwardButton = new JFXButton(" ");
        backButton.getStyleClass().addAll("back-button", "action");
        forwardButton.getStyleClass().addAll("forward-button", "action");




        JFXTextField urlTextField = new JFXTextField();
        urlTextField.setPromptText("URL or Search Term");;
        setTextFieldColors(urlTextField);


        HBox hBox = new HBox(backButton, forwardButton, urlTextField);
        hBox.setPadding(new Insets(5.0));
        hBox.setSpacing(5.0);
        setHgrow(urlTextField, Priority.ALWAYS);
        hBox.getStyleClass().add("primary-light-background");

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.locationProperty().addListener((v, oldValue, newValue) -> {
            urlTextField.setText(newValue);
        });
        urlTextField.setOnKeyReleased(e -> handleURLTextFieldHandleKeyTyped(e, urlTextField, engine));
        forwardButton.setOnMouseClicked(e -> {
            try {
                engine.getHistory().go(1);
            } catch (IndexOutOfBoundsException exception) {}
            e.consume();
        });
        backButton.setOnMouseClicked(e -> {
            try {
                engine.getHistory().go(-1);
            } catch (IndexOutOfBoundsException exception) {}
            e.consume();
        });

        VBox vBox = new VBox(hBox, webView);
        Tab tab = new Tab("New Tab", vBox);
        engine.titleProperty().addListener((v, oldValue, newValue) -> {
            tab.setText(newValue);
        });
        setVgrow(webView, Priority.ALWAYS);

        tabs.remove(plusTab);
        tabPane.getTabs().add(tab);
        tabs.add(plusTab);
        selection.selectPrevious();
    }

    private String convertToGoogleSearch(String input) {
        return "https://www.google.ch/search?q="+ input;
    }

    private void setTextFieldColors(JFXTextField... fields) {
        for (JFXTextField field : fields) {
            field.setUnFocusColor(Color.web(secondary));
            field.setFocusColor(Color.web(secondaryDark));
        }

    }
}
