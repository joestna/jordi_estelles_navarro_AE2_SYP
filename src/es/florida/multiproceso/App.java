package es.florida.multiproceso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class App {

	public static String[] nombreNEO;
	public static double[][] informacionNEO;
	
	public static void main(String[] args) {
		
		String nombreFicheroAnalizar = "NEOs.txt";
		
		// Contador de tiempo para contar el tiempo transcurrido
		long ICalcularTiempo = System.nanoTime();
		
		// Leer el fichero para contar cuantos NEO's hay en el
		int cantidadNEO = ContarNumeroNEOs( nombreFicheroAnalizar );
		
		
		// Dividir el numero de NEO's por la cantidad de nucleos disponibles en el procesador
		int nucleosDisponibles = Runtime.getRuntime().availableProcessors();
		int NEOsPorNucleo; //DEFINIR VARIABLES??
		
		boolean NEOsPorNucleoExactos;
		int ultimoNucleo = 0;
		
		if(cantidadNEO % nucleosDisponibles != 0)
		{
			NEOsPorNucleoExactos = false;
			int auxCantidadNEO = cantidadNEO;
			while(auxCantidadNEO % nucleosDisponibles != 0)
			{
				auxCantidadNEO--;
				ultimoNucleo++;
			}
			
			NEOsPorNucleo = auxCantidadNEO / nucleosDisponibles;
		}
		else
		{
			NEOsPorNucleoExactos = true;
			NEOsPorNucleo = cantidadNEO / nucleosDisponibles;
		}
		
		nombreNEO = new String[NEOsPorNucleo];
		informacionNEO = new double[3][NEOsPorNucleo];
		
		
		System.out.println( "NucleosExactos completo" );
		
		// Volver a recorrer el fichero, segun los calculos anteriores y guardar la informacion de cada lista en las variables globales
		GestionarInformacionNEOs( nombreFicheroAnalizar, NEOsPorNucleo, ultimoNucleo, NEOsPorNucleoExactos, nucleosDisponibles );
		
		
	
	// Leer el fichero, guardar su informacion en una lista
	
	// Dividir el numero de procesadores que tiene el procesador por el numero de lineas que hay en el fichero
	
	// Crear una lista que sera la informacion que se le pasara a cada proceso, dependiendo del numero de lineas que hay y el numero de procesadores
	// 8 lineas, 4 procesadores = 2 lineas por procesador -> estas dos lineas las almacenare en una lista y le pasare la lista al proceso al crearlo
	// de la lista principal se le pasaran las posiciones de las lineas que corresponden a cada procesador a la lista secundaria
	
	// hacer un for para crear el numero de procesos = numero de procesadores
	
	// procesar la informacion = guardar la informacion en un fichero independiente que se llamara como el NEO Procesado
	// se mostrara como salida la probabilidad de colision del neo con la tierra y si es mayor del 10% lanzara alerta mundial, si es menor tranquilidad
	// se mostrara como salida tambien el tiempo de ejecuccion del programa

	}
	
	public static int ContarNumeroNEOs( String nombreFichero ) 
	{
		int cantidadNEO = 0;
		
		try
		{
			FileInputStream fichero = new FileInputStream( nombreFichero ); // Falta poner el nombre del fichero
			InputStreamReader fich = new InputStreamReader( fichero );
			BufferedReader br = new BufferedReader( fich );
			String linea = br.readLine();
			
			while( linea != null )
			{
				cantidadNEO++;
				linea = br.readLine();
			}
			br.close();
			
			System.out.println( "ContarNUmeroNEOs completo" );
			
			return cantidadNEO;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return cantidadNEO;
	}
	
	
	
	public static void GestionarInformacionNEOs( String nombreFichero, int NEOsPorNucleo, int ultimoNucleo, boolean NEOsPorNucleoExactos, int nucleosDisponibles)
	{		
		try
		{
			System.out.println( "GestionarInformacion entrada" );
			
			ArrayList<String> NEOs = new ArrayList<String>();
			
			FileInputStream fichero = new FileInputStream( nombreFichero ); // Falta poner el nombre del fichero
			InputStreamReader fich = new InputStreamReader( fichero );
			BufferedReader br = new BufferedReader( fich );
			String linea = br.readLine();
			
			int contadorNEOsPorNucleo = 0;
			int contadorNucleosLlenos = 0;
			
			while( linea != null )
			{
				NEOs.add(linea);
				contadorNEOsPorNucleo++;
				
				if( contadorNEOsPorNucleo == nucleosDisponibles )
				{
					GuardarInformacionNEOs( NEOs );
					NEOs = new ArrayList<String>();

					contadorNucleosLlenos++;
					
					LanzarProcesos(nucleosDisponibles);
					
					contadorNEOsPorNucleo = 0;
				}
				else if( contadorNucleosLlenos == NEOsPorNucleo -1 && contadorNEOsPorNucleo == ultimoNucleo && !NEOsPorNucleoExactos )
				{
					GuardarInformacionNEOs( NEOs );
					NEOs = new ArrayList<String>();
					
					contadorNucleosLlenos++;
					
					LanzarProcesos(nucleosDisponibles);
					
					contadorNEOsPorNucleo = 0;
				}
			
				linea = br.readLine();
			}
			
			br.close();
			
			System.out.println( "Numero de nucleos completos" + contadorNucleosLlenos );
			
			System.out.println( "GestionarInformacion completo" );
			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void LanzarProcesos( int contadorNEOsPorNucleo )
	{
		for( int i = 0; i < contadorNEOsPorNucleo; i++ )
		{
			// ' clase ' sera nuestro proceso ( el proceso lanzado sera la clase Probabilidad con su metodo main y sus funcionalidades)
			// Paquete=package (es.florida.psp_neo) con la clase (Probabilidad)
			// La clase probabilidad tiene un metodo main ( cuando se lanza es como lanzar un ejecutable ) | La clase en su main se invoca a ella misma y utiliza sus propios metodos sin depender de otras
			String clase = "es.florida.multiproceso.CalculosNEO";
			File fichResultado = new File(nombreNEO[i]);
			try {
				// 
				String javaHome = System.getProperty("java.home"); // Donde se ubica javahome en el ordenador
			    String javaBin = javaHome + File.separator + "bin" + File.separator + "java"; // Donde esta el binario que ejecutara la aplicacion java ( el interprete de java )
			    String classpath = System.getProperty("java.class.path"); // Donde esta el classpath que es una propiedad del java development kit JDK
			    String className = clase; // Donde se encuentra la clase que queremos ejecutar ( paquete + clase )

			    ArrayList<String> command = new ArrayList<>();
			    command.add(javaBin);
			    command.add("-cp");
			    command.add(classpath);
			    command.add(className);
			    command.add(String.valueOf(informacionNEO[0][i]));
			    command.add(String.valueOf(informacionNEO[1][i]));
			    
			    ProcessBuilder builder = new ProcessBuilder(command); // Ensamblamos el codigo ( SERA EL COMANDO QUE QUEREMOS LANZAR COMO SI LO LANZARAMOS POR LA TERMINAL DE LINUX )

				// Guarda el resultado de la probabilidad ( que es el proceso lanzado ) en un fichero pasandole la ruta por parametro--> Probabilidad devuelve un System.out.println() con la probabilidad calculada
			    builder.redirectOutput(fichResultado);

				// Lanza proceso
				builder.start();
				
				System.out.println( "LanzarProcesos completo" );

				/* EL SIGUIENTE CODIGO COMENTADO NO ES DE ESTE EJERCICIO */
				/* builder.inheritIO().start(); */ // Con este codigo la salida por pantalla que mostraria el proceso en su contexto la mostraria aqui
				/* Process.waitFor(); */ // Esperaria a que el proceso terminara para poder continuar con el codigo
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	public static void GuardarInformacionNEOs( ArrayList<String> listaInformacion )
	{	
		int posicionAGuardar = 0;
		
		for( String linea : listaInformacion ) 
		{			
			String[] elementosLinea = linea.split(",");
			
			nombreNEO[posicionAGuardar] = elementosLinea[0];
			informacionNEO[0][posicionAGuardar] = Double.valueOf(elementosLinea[1]);
			informacionNEO[1][posicionAGuardar] = Double.valueOf(elementosLinea[2]);
			
			posicionAGuardar++;
		}
		
		System.out.println( "GuardarInformacion completo" );
	}
}
