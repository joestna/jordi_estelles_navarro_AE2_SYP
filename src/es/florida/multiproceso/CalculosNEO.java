package es.florida.multiproceso;

public class CalculosNEO {

	public static void main(String[] args) 
	{
		CalculosNEO calculos = new CalculosNEO();
		double posicion = Double.parseDouble(args[0]);
		double velocidad = Double.parseDouble( args[1]);
		double resultado = calculos.CalculoProbabilidad( posicion, velocidad );
		
		System.out.println( resultado );
	}
	
	public static double CalculoProbabilidad( double posicionNEO, double velocidadNEO )
	{
		double posicionTierra = 1;
		double velocidadTierra = 100;
		
		for (int i = 0; i < (50 * 365 * 24 * 60 * 60); i++) 
		{
			posicionNEO = posicionNEO + velocidadNEO * i;
			posicionTierra = posicionTierra + velocidadTierra * i;
		}
		
		double resultado = 100 * Math.random() * Math.pow( ((posicionNEO-posicionTierra)/(posicionNEO+posicionTierra)), 2);
		
		return resultado;
	}

}
