import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TetrisGame {
    private static final int FILAS = 20;
    private static final int COLUMNAS = 10;
    private static final int TAMANO_CELDA = 30;
    private static final int INTERVALO_CAIDA = 500; // 500 ms
    private Color piezaColorActual;
    private Timer timer;

    private Random random = new Random();
    // Agrega un campo para mantener el color original de la pieza actual
    private Color colorOriginalPiezaActual = null;
    // Agrega un campo para mantener un mapa de colores de las piezas en el tablero
    private Map<Point, Color> coloresPiezasFijas = new HashMap<>();

    private static final int[][] PIEZA_L = {
            {1, 0},
            {1, 0},
            {1, 1}
    };

    // Matriz para la pieza "J"
    private static final int[][] PIEZA_J = {
            {0, 1},
            {0, 1},
            {1, 1}
    };

    // Matriz para la pieza "I"
    private static final int[][] PIEZA_I = {
            {1},
            {1},
            {1},
            {1}
    };

    // Matriz para la pieza "O"
    private static final int[][] PIEZA_O = {
            {1, 1},
            {1, 1}
    };

    // Matriz para la pieza "S"
    private static final int[][] PIEZA_S = {
            {0, 1, 1},
            {1, 1, 0}
    };

    // Matriz para la pieza "Z"
    private static final int[][] PIEZA_Z = {
            {1, 1, 0},
            {0, 1, 1}
    };

    private static final int[][][] PIEZAS_DISPONIBLES = {PIEZA_L, PIEZA_J, PIEZA_I, PIEZA_O, PIEZA_S, PIEZA_Z};
    private Color[][] coloresTablero = new Color[FILAS][COLUMNAS];

    private Color obtenerColorAleatorio() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }

    private int[][] generarPiezaAleatoria() {
        int indiceAleatorio = random.nextInt(PIEZAS_DISPONIBLES.length);
        piezaColorActual = obtenerColorAleatorio(); // Genera un color aleatorio para la pieza
        return PIEZAS_DISPONIBLES[indiceAleatorio];
    }

    private void fijarPiezaEnTablero() {
        // Coloca la pieza actual en el tablero
        for (int fila = 0; fila < piezaActual.length; fila++) {
            for (int columna = 0; columna < piezaActual[0].length; columna++) {
                if (piezaActual[fila][columna] == 1) {
                    int filaEnTablero = piezaY + fila;
                    int columnaEnTablero = piezaX + columna;
                    tablero[filaEnTablero][columnaEnTablero] = true;

                    // Asigna el color al tablero desde el mapa de colores de piezas fijas
                    coloresTablero[filaEnTablero][columnaEnTablero] = piezaColorActual;
                }
            }
        }
    }

    private boolean esPosibleMoverPieza(int nuevaX, int nuevaY, int[][] nuevaPieza) {
        // Verifica que la nueva posición esté dentro de los límites del tablero
        if (nuevaX < 0 || nuevaX + nuevaPieza[0].length > COLUMNAS || nuevaY + nuevaPieza.length > FILAS) {
            return false; // Fuera de los límites del tablero
        }

        // Verifica si la nueva posición colisiona con otras piezas en el tablero
        for (int i = 0; i < nuevaPieza.length; i++) {
            for (int j = 0; j < nuevaPieza[i].length; j++) {
                if (nuevaPieza[i][j] == 1) {
                    int filaEnTablero = nuevaY + i;
                    int columnaEnTablero = nuevaX + j;

                    if (filaEnTablero >= 0 && tablero[filaEnTablero][columnaEnTablero]) {
                        return false; // Colisión con otra pieza
                    }
                }
            }
        }

        return true; // Movimiento válido
    }

    private int[][] rotarMatriz(int[][] matriz) {
        int filas = matriz.length;
        int columnas = matriz[0].length;
        int[][] nuevaMatriz = new int[columnas][filas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                nuevaMatriz[j][filas - 1 - i] = matriz[i][j];
            }
        }

        return nuevaMatriz;
    }


    private void moverPiezaDerecha() {
        // Verificar si es posible mover la pieza hacia la derecha
        if (esPosibleMoverPieza(piezaX + 1, piezaY, piezaActual)) {
            piezaX++; // Incrementar la posición X de la pieza
        }
    }

    private void moverPiezaIzquierda() {
        // Verificar si es posible mover la pieza hacia la izquierda
        if (esPosibleMoverPieza(piezaX - 1, piezaY, piezaActual)) {
            piezaX--; // Restar 1 a la posición X de la pieza
        }
    }

    private void rotarPieza() {
        // Implementa la lógica para rotar la pieza actual
        int[][] nuevaPieza = rotarMatriz(piezaActual);

        if (esPosibleMoverPieza(piezaX, piezaY, nuevaPieza)) {
            piezaActual = nuevaPieza;
        }
    }

    private JFrame frame;
    private JPanel gamePanel;
    private boolean[][] tablero;
    private int puntuacion = 0;
    private int piezaX, piezaY;
    private int[][] piezaActual;

    public TetrisGame() {
        frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(COLUMNAS * TAMANO_CELDA, FILAS * TAMANO_CELDA);

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarTablero(g);
                dibujarPiezaActual(g);
            }
        };
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setLayout(new GridLayout(FILAS, COLUMNAS));

        frame.add(gamePanel);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        moverPiezaIzquierda();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moverPiezaDerecha();
                        break;
                    case KeyEvent.VK_DOWN:
                        moverPiezaAbajo();
                        break;
                    case KeyEvent.VK_SPACE:
                        rotarPieza();
                        break;
                }
            }


            @Override
            public void keyReleased(KeyEvent e) {}
        });

        tablero = new boolean[FILAS][COLUMNAS];
        timer = new Timer(INTERVALO_CAIDA, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moverPiezaAbajo();
            }
        });

        iniciarJuego();
        frame.setVisible(true);
    }

    private void iniciarJuego() {
        // Inicializa el tablero
        tablero = new boolean[FILAS][COLUMNAS];

        // Inicializa la posición de la pieza actual
        piezaX = COLUMNAS / 2 - 1; // Centra la pieza en la parte superior del tablero
        piezaY = 0;

        // Inicializa la forma de la pieza actual (en este caso, la pieza "L")
        piezaActual = PIEZA_L;

        // Inicia el temporizador para que la pieza comience a caer automáticamente
        timer.start();
    }

    private void dibujarTablero(Graphics g) {
        // Dibuja el tablero con colores según las celdas ocupadas/vacías y el color correspondiente
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                if (tablero[fila][columna]) {
                    g.setColor(coloresTablero[fila][columna]); // Utiliza el color asignado en el tablero
                } else {
                    g.setColor(Color.GRAY); // Color de celdas vacías
                }
                g.fillRect(columna * TAMANO_CELDA, fila * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
                g.setColor(Color.BLACK);
                g.drawRect(columna * TAMANO_CELDA, fila * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            }
        }
    }

    private void dibujarPiezaActual(Graphics g) {
        // Obtén las dimensiones de la pieza
        int filas = piezaActual.length;
        int columnas = piezaActual[0].length;

        // Dibuja la pieza en el tablero con el color actual
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                if (piezaActual[fila][columna] == 1) {
                    g.setColor(piezaColorActual); // Utiliza el color asignado a la pieza actual
                    int x = (piezaX + columna) * TAMANO_CELDA;
                    int y = (piezaY + fila) * TAMANO_CELDA;
                    g.fillRect(x, y, TAMANO_CELDA, TAMANO_CELDA);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, TAMANO_CELDA, TAMANO_CELDA);
                }
            }
        }
    }

    private void eliminarFilasCompletas() {
        // Busca y elimina filas completas del tablero
        for (int fila = FILAS - 1; fila >= 0; fila--) {
            boolean filaCompleta = true;

            for (int columna = 0; columna < COLUMNAS; columna++) {
                if (!tablero[fila][columna]) {
                    filaCompleta = false;
                    break;
                }
            }

            if (filaCompleta) {
                // Desplaza las filas superiores hacia abajo
                for (int i = fila; i > 0; i--) {
                    for (int j = 0; j < COLUMNAS; j++) {
                        tablero[i][j] = tablero[i - 1][j];
                    }
                }

                // Llena la fila superior con celdas vacías
                for (int j = 0; j < COLUMNAS; j++) {
                    tablero[0][j] = false;
                }

                // Incrementa la puntuación (si lo deseas)
                puntuacion += 100; // Puedes ajustar la puntuación como desees
            }
        }
    }

    private void generarNuevaPieza() {
        // Actualiza el color original de la pieza actual
        colorOriginalPiezaActual = piezaColorActual;

        // Limpia el mapa de colores de piezas fijas
        coloresPiezasFijas.clear();

        // Genera una nueva pieza en la parte superior del tablero
        piezaX = COLUMNAS / 2 - 1;
        piezaY = 0;

        // Selecciona una pieza aleatoria de las disponibles
        piezaActual = generarPiezaAleatoria();

        // Detiene el temporizador para dar tiempo al jugador de manejar la nueva pieza
        timer.stop();

        // Verifica si el juego ha terminado (por ejemplo, si la nueva pieza colisiona inmediatamente)
        if (!esPosibleMoverPieza(piezaX, piezaY, piezaActual)) {
            // Juego terminado: Puedes implementar una lógica para mostrar un mensaje de "Game Over" o reiniciar el juego
            reiniciarJuego();
        } else {
            // Reinicia el temporizador para que la nueva pieza comience a caer
            timer.start();
        }
    }

    private void reiniciarJuego() {
        // Reinicia el tablero, puntuación y otras variables
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                tablero[fila][columna] = false; // Vacía el tablero
            }
        }
        puntuacion = 0; // Reinicia la puntuación
        // Puedes restablecer otras variables de estado del juego si las tienes

        // Inicia una nueva partida llamando a iniciarJuego()
        iniciarJuego();
    }

    private void moverPiezaAbajo() {
        // Verificar si es posible mover la pieza hacia abajo
        if (esPosibleMoverPieza(piezaX, piezaY + 1, piezaActual)) {
            piezaY++; // Incrementar la posición Y de la pieza
        } else {
            // La pieza ha llegado al fondo o ha colisionado con otra pieza
            fijarPiezaEnTablero(); // Coloca la pieza en el tablero
            eliminarFilasCompletas(); // Elimina filas completas si las hay
            generarNuevaPieza(); // Genera una nueva pieza para el juego
        }

        // Luego, llama a repaint() para actualizar la pantalla
        gamePanel.repaint();
    }

    // Otras funciones para gestionar piezas, colisiones y puntuación

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TetrisGame tetrisGame = new TetrisGame(); // Crear una instancia del juego
            }
        });
    }
}
