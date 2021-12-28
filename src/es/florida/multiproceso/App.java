package es.florida.multiproceso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class App {

	public static ArrayList<String> listaCompletaNombresNEO = new ArrayList<String>();
	public static String[] nombreNEO;
	public static double[][] informacionNEO;
	public static double[] probabilidades;
	
	public static void main(String[] args) {
		
		String nombreFicheroAnalizar = "NEOs.txt";
		
		// Contador de tiempo para contar el tiempo transcurrido
		long ICalcularTiempo = System.nanoTime();
		
		// Leer el fichero para contar cuantos NEO's hay en el
		int cantidadNEO = ContarNumeroNEOs( nombreFicheroAnalizar );
		probabilidades = new double[cantidadNEO];		
		
		// Dividir el numero de NEO's por la cantidad de nucleos disponibles en el procesador
		int nucleosDisponibles = Runtime.getRuntime().availableProcessors();
		int NEOsPorNucleo; //DEFINIR VARIABLES??
		
		
		// Verificar si el numero de NEOs entre el numero de cores disponbles es par o inpar
		// Si es impar el ultimo grupo de procesos lanzados sera menor
		boolean NEOsPorNucleoExactos;
		int ultimoGrupoProcesos = 0;
		
		if(cantidadNEO % nucleosDisponibles != 0)
		{
			NEOsPorNucleoExactos = false;
			int auxCantidadNEO = cantidadNEO;
			while(auxCantidadNEO % nucleosDisponibles != 0)
			{
				auxCantidadNEO--;
				ultimoGrupoProcesos++;
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
		
		
		// Volver a recorrer el fichero, segun los calculos anteriores y guardar la informacion de cada linea en las variables globales
		GestionarInformacionNEOs( nombreFicheroAnalizar, NEOsPorNucleo, ultimoGrupoProcesos, NEOsPorNucleoExactos, nucleosDisponibles );
		
		
		// Comprobar que los procesos han terminado su tarea
		boolean finalProcesos = false;
		while( !finalProcesos )
		{
			try
			{
				GetResultadosFicheros();
				finalProcesos = true;
			}
			catch( Exception e )
			{
				
			}
		}
		
		
		// Generar una alerta dependiendo del peligro que supone el NEO
		NotificarPeligro();
	}
	
	
	// Cuenta el numero de NEOs en el fichero inicial
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
			
			System.out.println( "ContarNumeroNEOs completo" );
			
			return cantidadNEO;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return cantidadNEO;
	}
	
	
	// Utiliza la cantidad de cores disponibles y la cantidad de NEOs a gestionar
	// Crea los bloques de neos que seran del numero de cores disponibles, guarda la informacion del grupo y lanza los procesos
	// Se repite el proceso por numero de grupos que hayan salido de las operaciones de main
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
			
			int contadorNEOs = 0;
			int contadorNucleosLlenos = 0;
			
			while( linea != null )
			{
				NEOs.add(linea);
				contadorNEOs++;
				
				if( contadorNEOs == nucleosDisponibles )
				{
					GuardarInformacionNEOs( NEOs );
					NEOs = new ArrayList<String>();

					contadorNEOs++;
					
					LanzarProcesos(nucleosDisponibles);
					
					contadorNEOs = 0;
				}
				else if( contadorNucleosLlenos == NEOsPorNucleo -1 && contadorNEOs == ultimoNucleo && !NEOsPorNucleoExactos )
				{
					GuardarInformacionNEOs( NEOs );
					NEOs = new ArrayList<String>();
					
					contadorNucleosLlenos++;
					
					LanzarProcesos(nucleosDisponibles);
					
					contadorNEOs = 0;
				}
			
				linea = br.readLine();
			}
			
			br.close();
			
			System.out.println( "Numero de combinaciones de procesos lanzados " + contadorNucleosLlenos );
			
			System.out.println( "GestionarInformacion completo" );
			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	// Se lanzan los procesos y se redirige la salida del proceso a un fichero con el nombre del NEO
	public static void LanzarProcesos( int contadorNEOsPorNucleo )
	{
		for( int i = 0; i < contadorNEOsPorNucleo; i++ )
		{
			String clase = "es.florida.multiproceso.CalculosNEO";
			File fichResultado = new File(nombreNEO[i]);
			
			try {
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
					
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	
	// Se guarda la informacion en las variables globales del grupo de NEOs almacenados en la lista que dependeran del numero de cores disponibles
	public static void GuardarInformacionNEOs( ArrayList<String> listaInformacion )
	{	
		int posicionAGuardar = 0;
		
		for( String linea : listaInformacion ) 
		{			
			String[] elementosLinea = linea.split(",");
			
			nombreNEO[posicionAGuardar] = elementosLinea[0];
			informacionNEO[0][posicionAGuardar] = Double.valueOf(elementosLinea[1]);
			informacionNEO[1][posicionAGuardar] = Double.valueOf(elementosLinea[2]);
			
			listaCompletaNombresNEO.add(elementosLinea[0]);
			
			posicionAGuardar++;
		}
		
		System.out.println( "GuardarInformacion completo" );
	}
	
	
	// Lee los ficheros creados a partir de los procesos con el nombre de cada uno de los NEO
	// Estos ficheros contienen las probabilidades de colision del NEO con la tierra
	// Las probabilidades se guardaran en el array global probabilidades
	public static void GetResultadosFicheros() throws Exception
	{	
		double probabilidad = 0;
		for( int i = 0; i < listaCompletaNombresNEO.size(); i++ ) 
		{				
			FileInputStream fichero = new FileInputStream(listaCompletaNombresNEO.get(i));
			InputStreamReader fir = new InputStreamReader(fichero);
			BufferedReader br = new BufferedReader(fir);
			
			String linea = br.readLine();
			probabilidad = Double.parseDouble(linea);
			
			probabilidades[i] = probabilidad;
		}
	}
	
	
	// Recorre el array global probabilidades y lanza alertas segun la probabilidad de colision del NEO con la tierra
	public static void NotificarPeligro()
	{
		for( int i = 0 ; i < listaCompletaNombresNEO.size(); i++ )
		{
			System.out.print( "> NEO : " + listaCompletaNombresNEO.get(i) + " >> " );
			
			if( probabilidades[i] > 10 )
			{
				System.out.println( "P E L I G R O DE COLISION !!! | LA RAZA HUMANA ESTA EN PELIGRO DE EXTINCION" );
			}
			else
			{
				System.out.println( "No hay peligro | Podemos dormir tranquilos" );
			}
		}
	}
}
