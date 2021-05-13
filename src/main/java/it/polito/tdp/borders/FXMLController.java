
package it.polito.tdp.borders;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ComboBox;
import it.polito.tdp.borders.model.Country;
import it.polito.tdp.borders.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtAnno"
    private TextField txtAnno; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    @FXML
    private ComboBox<Country> comboBox;
    
    @FXML
    private Button btnStatiRaggiungibili;

    @FXML
    void doCalcolaConfini(ActionEvent event) {
    	String stringAnno = this.txtAnno.getText();
    	int anno=0;
    	try {
    		anno = Integer.parseInt(stringAnno);
    	}
    	catch(NumberFormatException nbe) {
    		this.txtResult.setText("ERRORE! Il valore inserito non è numerico!");
    		return;
    	}
    	
    	if(anno>=1816 && anno<=2016) {
    		model.creaGrafo(anno);
    		
    		String numeroVerticiArchi = model.getVerticesAndEdges();
    		this.txtResult.setText(numeroVerticiArchi);
    		
    		String numeroComponentiConnesse = model.getNumberOfConnectedComponents();
    		this.txtResult.appendText(numeroComponentiConnesse);
    		
    		String numeroConfiniVeritici = model.getCountryAndBorders();
    		this.txtResult.appendText(numeroConfiniVeritici);
    		
    		// Abilito il buttone Stati Raggiungibili e inserisco gli elementi nella comboBox   
    		this.btnStatiRaggiungibili.setDisable(false);
    		this.comboBox.getItems().addAll(model.getAllCountries());
    	}
    	else {
    		this.txtResult.setText("ERRORE! L'anno inserito non è valido!");
    		return;
    	}
    }
    
    @FXML
    void doStatiRaggiungibili(ActionEvent event) {
    	Country selezionato = this.comboBox.getValue();
    	String risultato = model.fermateRaggiungibiliBFV(selezionato);
    	
    	this.txtResult.setText(risultato);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert comboBox != null : "fx:id=\"comboBox\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnStatiRaggiungibili != null : "fx:id=\"btnStatiRaggiungibili\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
