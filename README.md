# GoogleMapsApplicationWithKotlin

En este repositorio está el código fuente de una aplicación en Kotlin que utiliza
Google Maps para detectar la posición del usuario en un mapa 
Tambien se puede conseguir el APK que contiene la aplicación que se deseaba.
El APK tiene el siguiente nombre: app-debug.apk

Este APK esta en la raiz del proyecto. 

Para correr el apk, se debe instalar en un teléfono que utilice android.
La primera vez que se inicie la aplicación esta solicitará permiso para utilizar el
gps del teléfono inteligente en el que esté instalado.

Como indican las instrucciones de la prueba, la aplicación coloca dos marcadores,
uno señalando a Mi Aguila en Bogotá y otro señalando al Parque Virrey en la misma ciudad.
A medida que una persona recorra el camino entre estos dos puntos la aplicación
colocara un marcador en la posición en la que está el usuario. Este evento sucedera cada 3 segundos.

La aplicación también tiene un medidor de velocidad, en la parte inferior de la pantalla se deberia 
poder observar una ventana circular con texto que indica la velocidad estimada del usuario en
metros sobre segundos.

Bugs Menores Conocidos:

La primera vez que se ejecuta la aplicación, cuando pide el permiso para utilizar el GPS,
la aplicación no está mostrando la localización del usuario. Cerrar y abrir la aplicación
una vez que se le dió el permiso soluciona el problema.

Autor: Sergio Barrios
