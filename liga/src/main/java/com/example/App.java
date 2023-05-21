package com.example;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class App {

    private final static int ANIO = 2023;

    public static void main(String[] args) throws InterruptedException {
        // Obtener la ruta absoluta del controlador de Microsoft Edge, que en este caso
        // esta en la carpeta del proyecto
        String rutaDriver = System.getProperty("user.dir") + "/drivers/msedgedriver.exe";

        // Indicamos el controlador que se va a usar
        System.setProperty("webdriver.edge.driver", rutaDriver);

        // Se crea un driver con el controlador indicado
        WebDriver driver = new EdgeDriver();

        // Abre Google
        driver.get("https://www.google.com");

        // Esperar a que cargue el mensaje de las cookies
        Thread.sleep(2000);

        // Si muestra el mensaje de cookies que las acepte
        if (driver.findElement(By.id("L2AGLb")).isDisplayed()) {
            driver.findElement(By.id("L2AGLb")).click();
        }

        // Localizamos el campo de busqueda (en Google se llama q)
        WebElement busqueda = driver.findElement(By.name("q"));

        // Hacemos una busqueda
        busqueda.sendKeys("Liga " + ANIO + " tabla de posiciones");

        // Enviar el formulario de busqueda
        busqueda.submit();

        // Esperar para que aparezcan los resultados
        Thread.sleep(1000);

        // Encuentra el elemento que contiene el primer resultado de la tabla
        WebElement tabla = driver.findElement(By.className("Jzru1c"));

        List<WebElement> filas = tabla.findElements(By.tagName("tr"));

        WebElement primero = filas.get(1);

        List<WebElement> celdas = primero.findElements(By.tagName("td"));

        System.out.println("El equipo ganador: " + celdas.get(2).getText());
        System.out.println("Ha ganado con " + celdas.get(10).getText() + " puntos");

        // Cerrar el navegador
        driver.quit();
    }
}
