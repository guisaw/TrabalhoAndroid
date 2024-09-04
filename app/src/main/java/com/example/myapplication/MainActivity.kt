package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen(onExitApp = { finish() }) // Passa a função de saída do aplicativo
        }
    }
}

@Composable
fun GameScreen(onExitApp: () -> Unit) {
    // Define o estado do jogo
    var targetClicks by remember { mutableStateOf(Random.nextInt(1, 51)) }
    var clickCount by remember { mutableStateOf(0) }
    var gameStatus by remember { mutableStateOf("Comece sua jornada!") }
    var gameOver by remember { mutableStateOf(false) }
    var hasGivenUp by remember { mutableStateOf(false) }
    var imageResource by remember { mutableStateOf(R.drawable.ovo_inteiro) }

    // Imagens para a transição do ovo
    val images = listOf(
        R.drawable.ovo_inteiro,   // 0 cliques
        R.drawable.ovo_riscado,   // 1 intervalo
        R.drawable.ovo_rachado,   // 2 intervalos
        R.drawable.ovo_chocado    // 3 intervalos
    )

    // Função para redefinir o jogo
    fun resetGame() {
        targetClicks = Random.nextInt(1, 51)
        clickCount = 0
        gameStatus = "Comece sua jornada!"
        gameOver = false
        hasGivenUp = false
        imageResource = R.drawable.ovo_inteiro
    }

    // Atualiza a imagem de acordo com o número de cliques
    fun updateImageResource() {
        when {
            hasGivenUp -> imageResource = R.drawable.ovo_quebrado
            clickCount >= targetClicks -> imageResource = R.drawable.pintinho
            else -> {
                val index = when {
                    clickCount < targetClicks / 4 -> 0 // 0 a (targetClicks / 4)
                    clickCount < targetClicks / 2 -> 1 // (targetClicks / 4) a (targetClicks / 2)
                    clickCount < 3 * targetClicks / 4 -> 2 // (targetClicks / 2) a (3 * targetClicks / 4)
                    else -> 3 // 3 * targetClicks / 4 a targetClicks
                }
                imageResource = images[index]
            }
        }
    }

    // Atualiza o recurso da imagem toda vez que o número de cliques muda
    LaunchedEffect(clickCount) {
        updateImageResource()
    }

    Box(
        modifier = Modifier
            .fillMaxSize() // Preenche toda a tela
    ) {
        // Imagem de fundo redimensionada
        Image(
            painter = painterResource(id = R.drawable.fazenda),
            contentDescription = "Imagem de Fundo",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentScale = ContentScale.Crop // Ajusta a imagem para preencher a tela
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = "Descrição da Imagem",
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                gameOver -> {
                    Text(text = "Conquista alcançada!")
                    Button(onClick = {
                        resetGame()
                    }) {
                        Text("Jogar Novamente")
                    }
                }
                hasGivenUp -> {
                    Text(text = "Você desistiu.")
                    Button(onClick = {
                        resetGame()
                    }) {
                        Text("Novo Jogo")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onExitApp) {
                        Text("Sair")
                    }
                }
                else -> {
                    Text(text = "Cliques: $clickCount / $targetClicks")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        clickCount++
                        if (clickCount >= targetClicks) {
                            gameStatus = "Você alcançou sua conquista!"
                            gameOver = true
                        }
                    }) {
                        Text("Clique para Progredir")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = gameStatus)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        hasGivenUp = true
                        gameStatus = "Você desistiu."
                        imageResource = R.drawable.ovo_quebrado
                    }) {
                        Text("Desistir")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GameScreen(onExitApp = {}) // Chama a tela do jogo sem a função de sair
}
