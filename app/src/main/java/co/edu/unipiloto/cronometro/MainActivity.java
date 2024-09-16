package co.edu.unipiloto.cronometro;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private boolean running;
    private int segundos;
    private int milisegundos = 0;
    private final List<String> vueltas = new ArrayList<>();
    private int tiempoVueltaAnterior = 0;

    private Handler handler;
    private Runnable runnable;

    private int segundos2 = 0;
    private int milisegundos2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                actualizarCronometroPrincipal();
                actualizarCronometroSecundario();
                handler.postDelayed(this, 10);
            }
        };
        handler.post(runnable);
    }

    private void actualizarCronometroPrincipal() {
        TextView timeView = findViewById(R.id.timeView);
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int secs = segundos % 60;
        int millis = (milisegundos / 10) % 100;

        String tiempo = String.format(Locale.getDefault(), "%d:%02d:%02d:%02d", horas, minutos, secs, millis);
        timeView.setText(tiempo);

        if (running) {
            milisegundos += 10;
            segundos = milisegundos / 1000;
        }
    }

    private void actualizarCronometroSecundario() {
        TextView timeVueltaView = findViewById(R.id.timeVuelta);
        int horas = segundos2 / 3600;
        int minutos = (segundos2 % 3600) / 60;
        int secs = segundos2 % 60;
        int millis = (milisegundos2 / 10) % 100;

        String tiempo = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", horas, minutos, secs, millis);
        timeVueltaView.setText(tiempo);

        if (running) {
            milisegundos2 += 10;
            segundos2 = milisegundos2 / 1000;
        }
    }

    public void onClickIniciar(View view) { running = true; }
    public void onClickPausar(View view) { running = false; }
    public void onClickReiniciar(View view) {
        running = false;
        segundos = 0;
        milisegundos = 0;
        tiempoVueltaAnterior = 0;
        vueltas.clear();

        segundos2 = 0;
        milisegundos2 = 0;

        TableLayout tableLayout = findViewById(R.id.tablaVueltas);
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

        findViewById(R.id.timeVuelta).setVisibility(View.INVISIBLE);
        findViewById(R.id.scrollView).setVisibility(View.INVISIBLE);
    }

    private void agregarFilaTabla(int numeroVuelta, String tiempoParcial, String tiempoTotal) {
        TableLayout tableLayout = findViewById(R.id.tablaVueltas);
        TableRow fila = new TableRow(this);

        TextView columnaNumero = new TextView(this);
        columnaNumero.setText(String.valueOf(numeroVuelta));
        columnaNumero.setPadding(8, 8, 8, 8);
        columnaNumero.setTextSize(18);
        columnaNumero.setGravity(Gravity.CENTER);

        TextView columnaParcial = new TextView(this);
        columnaParcial.setText(tiempoParcial);
        columnaParcial.setPadding(8, 8, 8, 8);
        columnaParcial.setTextSize(18);
        columnaParcial.setGravity(Gravity.CENTER);

        TextView columnaTotal = new TextView(this);
        columnaTotal.setText(tiempoTotal);
        columnaTotal.setPadding(8, 8, 8, 8);
        columnaTotal.setTextSize(18);
        columnaTotal.setGravity(Gravity.CENTER);

        fila.addView(columnaNumero);
        fila.addView(columnaParcial);
        fila.addView(columnaTotal);

        tableLayout.addView(fila);
    }

    public void onClickVuelta(View view) {
        if (running) {
            if (vueltas.isEmpty()) {
                findViewById(R.id.timeVuelta).setVisibility(View.VISIBLE);
                findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            }

            int tiempoVuelta = segundos - tiempoVueltaAnterior;
            int horasVuelta = tiempoVuelta / 3600;
            int minutosVuelta = (tiempoVuelta % 3600) / 60;
            int secsVuelta = tiempoVuelta % 60;
            int millisVuelta = (milisegundos / 10) % 100;

            String tiempoParcial = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", horasVuelta, minutosVuelta, secsVuelta, millisVuelta);

            int horasTotal = segundos / 3600;
            int minutosTotal = (segundos % 3600) / 60;
            int secsTotal = segundos % 60;
            int millisTotal = (milisegundos / 10) % 100;
            String tiempoTotal = String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", horasTotal, minutosTotal, secsTotal, millisTotal);

            tiempoVueltaAnterior = segundos;

            agregarFilaTabla(vueltas.size() + 1, tiempoParcial, tiempoTotal);

            vueltas.add(tiempoParcial);

            segundos2 = 0;
            milisegundos2 = 0;
        }
    }
}