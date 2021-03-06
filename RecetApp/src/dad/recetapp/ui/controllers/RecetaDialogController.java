package dad.recetapp.ui.controllers;

import dad.recetapp.services.ServiceException;
import dad.recetapp.services.ServiceLocator;
import dad.recetapp.services.items.SeccionItem;
import dad.recetapp.ui.SeccionTab;
import dad.recetapp.services.items.CategoriaItem;
import dad.recetapp.services.items.RecetaItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jfxtras.scene.control.ListSpinner;
import jfxtras.scene.control.ListSpinnerIntegerList;
import jfxtras.util.StringConverterFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;


public class RecetaDialogController implements IDialogController<RecetaItem> {
	public static final String NEW_CAPTION = "Añadir";
	public static final String EDIT_CAPTION = "Guardar cambios";

	@FXML private Parent rootPane;
	@FXML private Button aceptarButton;
	@FXML private TextField paraText;
	@FXML private TextField nombreText;
	@FXML private ComboBox<String> paraCombo;
	@FXML private ComboBox<CategoriaItem> categoriaCombo; //en principio, CategoriaItem
	@FXML private GridPane topGridPane;
	@FXML private TabPane seccionTabPane;
	@FXML private Tab newTab;

	private ListSpinner<Integer> totalMinutosSpinner;
	private ListSpinner<Integer> thermoMinutosSpinner;
	private ListSpinner<Integer> totalSegundosSpinner;
	private ListSpinner<Integer> thermoSegundosSpinner;

	private Optional<RecetaItem> receta = Optional.empty();


	private boolean validate() {
		boolean valid = true;
		return valid;
	}

	@FXML public void onAceptarButtonClick() {
		if (validate()) {
			RecetaItem ri = receta.orElse(new RecetaItem());
			//TODO guardar cosas en ri
			receta = Optional.of(ri);
			Stage s = (Stage) rootPane.getScene().getWindow();
			s.close();
		}
	}

	@FXML public void onCancelarButtonClick() {
		Stage s = (Stage) rootPane.getScene().getWindow();
		s.close();
	}

	@FXML public void onNewTabSelection() {
		addTab();
		seccionTabPane.getSelectionModel().selectPrevious();
	}

	private void addTab() {
		addTab(Optional.empty());
	}

	private void addTab(Optional<SeccionItem> item) {
		SeccionTab tab = new SeccionTab();
		createTabContents(tab, item);
		seccionTabPane.getTabs().add(seccionTabPane.getTabs().size() - 1, tab);
	}

	private void createTabContents(SeccionTab tab, Optional<SeccionItem> item) {
		Task<BorderPane> task = new Task<BorderPane>() {
			@Override
			protected BorderPane call() {
				BorderPane tabContents = null;
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/dad/recetapp/ui/fxml/recetaTabContent.fxml"));
				RecetaTabContentController controller = new RecetaTabContentController()
						.withParentTab(tab)
						.withParentTabPane(seccionTabPane);
				if (item.isPresent())
					controller.setItem(item);
				loader.setController(controller);
				try {
					tabContents = loader.load();
				}
				catch (IOException e) {
					System.err.println("FXML TabContent noped, " + e.getMessage() + " cause: " + e.getCause());
				}
				tab.setController(controller);
				return tabContents;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				tab.setContent(getValue());
			}
		};

		task.run();
		//TODO Task ejecuta call() en hilo de eventos, usar otra clase
	}

	/*
	Por lo pronto, parece que no se pueden asignar limites ni StringConverters via FXML, asi que hay que meterlos programaticamente.
	 */
	private void initSpinners() {
		totalMinutosSpinner = new ListSpinner<>(new ListSpinnerIntegerList(0, 10000)).withEditable(true).withCyclic(true).withStringConverter(StringConverterFactory.forInteger());
		totalSegundosSpinner = new ListSpinner<>(new ListSpinnerIntegerList(0, 59)).withEditable(true).withCyclic(true).withStringConverter(StringConverterFactory.forInteger());
		thermoMinutosSpinner = new ListSpinner<>(new ListSpinnerIntegerList(0, 10000)).withEditable(true).withCyclic(true).withStringConverter(StringConverterFactory.forInteger());
		thermoSegundosSpinner = new ListSpinner<>(new ListSpinnerIntegerList(0, 59)).withEditable(true).withCyclic(true).withStringConverter(StringConverterFactory.forInteger());

		topGridPane.add(totalMinutosSpinner, 3, 0);
		topGridPane.add(totalSegundosSpinner, 5, 0);
		topGridPane.add(thermoMinutosSpinner, 3, 1);
		topGridPane.add(thermoSegundosSpinner, 5, 1);
	}

	private void initCombos() {
		paraCombo.getItems().add("Personas"); //que mas se supone que puede ir aqui?
		paraCombo.setValue("Personas");

		CategoriaItem ci = new CategoriaItem();
		ci.setDescripcion("<Seleccione una categoria>"); //TODO modificar toString()'s de los Items para que solo devuelvan su campo String.
		categoriaCombo.setValue(ci);
		Task<ObservableList<CategoriaItem>> task = new Task<ObservableList<CategoriaItem>>() {
			@Override
			protected ObservableList<CategoriaItem> call() {
				CategoriaItem[] c = new CategoriaItem[0];
				try {
					c = ServiceLocator.getCategoriasService().listarCategoria();
				}
				catch (ServiceException e) {
					System.err.println("CategoriaService Error: " + e.getMessage() + " Cause: " + e.getCause());
				}
				return FXCollections.observableArrayList(Arrays.asList(c));
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				categoriaCombo.getItems().addAll(getValue());
			}
		};
		task.run();
	}


	public void initialize(URL location, ResourceBundle resources) {
		initSpinners();
		initCombos();

		newTab.setGraphic(new ImageView(getClass().getResource("/dad/recetapp/ui/images/addTabIcon.png").toString())); //intente ponerlo via CSS, no lo consegui

		aceptarButton.setText(NEW_CAPTION);
	}

	@Override
	public void setItem(Optional<RecetaItem> item) {
		RecetaItem ri = item.get();
		receta = Optional.of(ri);
		aceptarButton.setText(EDIT_CAPTION);

		paraText.setText(ri.getCantidad().toString());
		paraCombo.setValue(ri.getPara()); //ri.getPara es lo que va en el combobox?
		nombreText.setText(ri.getNombre());
		//TODO una vez que RecetaItem cambie de idCategoria a CategoriaItem, quitar el tumor éste
		categoriaCombo.setValue(ri.getCategoria());

		totalSegundosSpinner.setValue(ri.getTiempoTotal() % 60);
		totalMinutosSpinner.setValue(ri.getTiempoTotal() / 60);
		thermoSegundosSpinner.setValue(ri.getTiempoThermomix() % 60);
		thermoMinutosSpinner.setValue(ri.getTiempoThermomix() / 60);

		seccionTabPane.getTabs().clear();
		seccionTabPane.getTabs().add(newTab);
		System.out.println("ri secciones " + ri.getSecciones());
		if (ri.getSecciones() != null)
			ri.getSecciones().forEach(seccionItem -> addTab(Optional.of(seccionItem)));
	}

	@Override
	public Optional<RecetaItem> getItem() {
		return receta;
	}
}
