package dad.recetapp;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;



import dad.recetapp.services.RecetaItem;
import dad.recetapp.services.ServiceException;
import dad.recetapp.services.ServiceLocator;



public class Main {
	public static void main(String[] args) throws SQLException {
		crearReceta();
		eliminarReceta();
		listarRecetas();
		obtenerReceta();
		buscarReceta();
		modificarReceta();
		listarRecetas();

	}

	private static void modificarReceta() {
		RecetaItem receta=new RecetaItem();
		receta.setId(3);
		receta.setNombre("Pollo al pilpil");
		receta.setFechaCreacion(new GregorianCalendar(2012,3,14).getTime());
		receta.setCantidad(2);
		receta.setIdCategoria(1);
		receta.setPara("2 personas");
		receta.setTiempoTotal(50);
		receta.setTiempoThermomix(25);
		try{
			ServiceLocator.getRecetasService().modificarReceta(receta);
		}catch(ServiceException e){
			System.out.println("Ha ocurrido un error: "+e.getMessage());
		}
	}

	private static void buscarReceta() {
		try {
			List<RecetaItem>recetas=ServiceLocator.getRecetasService().buscarRecetas("p", 2, 1L);
			System.out.println(recetas);
		} catch (Exception e) {
			System.err.println("Ha ocurrido un error:\n" +e.getMessage());
		}
	}

	private static void obtenerReceta() {
		try {
			RecetaItem receta = ServiceLocator.getRecetasService().obtenerReceta(1L);
			System.out.println(receta.getIdCategoria());
		} catch (Exception e) {
			System.err.println("Ha ocurrido un error:\n" +e.getMessage());
		}
		
	}

	private static void eliminarReceta() {
		try {
			ServiceLocator.getRecetasService().eliminarReceta(2L);
		} catch (Exception e) {
			System.err.println("Ha ocurrido un error:\n" +e.getMessage());
		}
	}
		
	

	private static void listarRecetas() {
		try {
			List<RecetaItem>recetas=ServiceLocator.getRecetasService().listarRecetas();
			System.out.println(recetas);
		} catch (Exception e) {
			System.err.println("Ha ocurrido un error:\n" +e.getMessage());
		}
	}

	private static void crearReceta() {
		RecetaItem receta=new RecetaItem();
		receta.setNombre("Pollo al curry");
		receta.setFechaCreacion(new GregorianCalendar(2014,3,14).getTime());
		receta.setCantidad(4);
		receta.setIdCategoria(2);
		receta.setPara("4 personas");
		receta.setTiempoTotal(50);
		receta.setTiempoThermomix(25);
		try{
			ServiceLocator.getRecetasService().crearReceta(receta);
		}catch(ServiceException e){
			System.out.println("Ha ocurrido un error: "+e.getMessage());
		}
	}
}
