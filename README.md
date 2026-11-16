# Geometry Runner

Geometry Runner é um jogo de plataforma 2D em Java Swing inspirado em jogos de corrida rítmica. O personagem avança automaticamente pelo cenário, e o jogador precisa sincronizar os saltos para evitar obstáculos e chegar ao fim de cada fase.

O projeto demonstra o uso de Java e Programação Orientada a Objetos em um jogo completo: menu, seleção de fases e aparências, física, colisões, animações, efeitos visuais e estados de vitória e derrota.

## Funcionalidades

- Menu principal com seleção de fase e prévia das aparências.
- Três aparências selecionáveis: Classic Blue, Fire Red e Neon Green.
- Fases 1 e 2 disponíveis; a Fase 3 aparece bloqueada no seletor.
- Fase 1 com mapa fixo, dificuldade progressiva e duração aproximada de 24 segundos.
- Fase 2, com duração aproximada de 42 segundos, mais obstáculos e dificuldade maior.
- Sequência de oito blocos em escada na Fase 2.
- Portais que invertem e restauram a gravidade na Fase 2; o personagem corre pelo teto durante a inversão.
- Espinhos e blocos com colisões adequadas ao chão e ao teto.
- Saltos com gravidade, preparação visual, rotação, impacto de aterrissagem e movimento sutil no chão.
- Rastros de partículas com cores e formas próprias para cada aparência, além de efeitos de aterrissagem, colisão e vitória.
- Telas de derrota e vitória, com opções de reinício e retorno ao lobby.
- Mapas determinísticos: a sequência de obstáculos não é aleatória.

## Tecnologias

- Java 17 ou superior.
- Java Swing e AWT para janela, entrada e desenho 2D.
- HTML5 Canvas e JavaScript na versão alternativa de navegador `jogar.html`.
- Nenhuma biblioteca externa é necessária para a versão Java.
- A versão de navegador mantém sua própria implementação JavaScript; alterações em fases e colisões devem ser mantidas em sincronia com o Java.

## Estrutura do projeto

```text
.
├── src/main/java/com/example/geometrydash/
│   ├── Main.java
│   ├── Game.java, GamePanel.java, GameView.java
│   ├── MainMenuPanel.java
│   ├── Player.java, PlayerSkin.java, JumpAnimation.java
│   ├── Particle.java, ParticleSystem.java, ParticleStyle.java
│   ├── Level.java, LevelManager.java, LevelOption.java
│   ├── GravityDirection.java, GravityPortal.java
│   ├── Obstacle.java, Spike.java, Block.java
│   ├── CollisionDetector.java, GameState.java
├── src/test/java/com/example/geometrydash/GameModelChecks.java
├── jogar.html
├── .gitignore
└── README.md
```

### Responsabilidade das classes

- `Main`: inicia a aplicação Swing e cria a janela.
- `GamePanel`: alterna entre lobby e partida.
- `MainMenuPanel`: apresenta as fases, as três aparências e a prévia do personagem.
- `GameView`: desenha a partida, recebe os controles e mostra os resultados.
- `Game`: coordena jogador, fase, estado, animações, partículas e regras da partida.
- `Player`: mantém posição, velocidade, gravidade, salto e hitbox física.
- `PlayerSkin`: define cores, partículas e formato visual de cada aparência.
- `JumpAnimation`: controla squash, rotação do salto, transição de gravidade, aterrissagem e movimento no chão.
- `Particle` e `ParticleSystem`: representam e atualizam partículas; o sistema limita a quantidade ativa.
- `LevelManager`: seleciona e carrega o mapa correspondente à fase escolhida.
- `Level`: contém a sequência fixa de obstáculos, portais, câmera e progresso.
- `LevelOption`: fornece os dados e a disponibilidade exibidos no seletor.
- `GravityDirection` e `GravityPortal`: representam a direção da gravidade e as transições entre chão e teto.
- `Obstacle`: abstração dos elementos que podem ser desenhados e colididos.
- `Spike` e `Block`: especializações de obstáculo com formas e colisões próprias.
- `CollisionDetector`: compara a hitbox física do jogador com as formas de colisão.
- `GameState`: enumera lobby, partida, derrota e vitória.

### Programação Orientada a Objetos

- **Encapsulamento:** atributos do jogo são privados e acessados por métodos definidos pelas classes.
- **Herança:** `Spike` e `Block` herdam de `Obstacle`.
- **Polimorfismo:** cada tipo de obstáculo desenha sua própria forma e fornece sua própria geometria de colisão.
- **Abstração:** `Obstacle` define a estrutura comum sem expor como cada forma é desenhada.
- **Composição:** `Game` coordena objetos especializados, como `Player`, `LevelManager`, `JumpAnimation` e `ParticleSystem`.

As transformações da animação não alteram a hitbox. As partículas também não participam das colisões. Na Fase 2, os portais alteram a aceleração, a direção do impulso do salto e a superfície de aterrissagem sem teletransportar o personagem.

## Como executar

### Requisitos

- JDK 17 ou superior, que inclui `javac` e `java`.
- Visual Studio Code com o Extension Pack for Java, se desejar executar pelo editor.

### Visual Studio Code

1. Abra no Visual Studio Code a pasta do repositório, que contém `README.md` e `src/`.
2. Aguarde a extensão Java reconhecer o projeto.
3. Abra `src/main/java/com/example/geometrydash/Main.java`.
4. Selecione **Run Java** acima do método `main`.

### Compilar e executar pelo PowerShell

Abra o terminal na pasta `geometry-dash-java` e execute:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
java -cp out com.example.geometrydash.Main
```

### Compilar as verificações automatizadas

As verificações do modelo não usam bibliotecas externas. Na raiz do repositório, compile primeiro o jogo e depois as verificações:

```powershell
$main = Get-ChildItem -Recurse src/main/java -Filter *.java
javac -encoding UTF-8 -d out $main.FullName
$checks = Get-ChildItem -Recurse src/test/java -Filter *.java
javac -encoding UTF-8 -cp out -d out $checks.FullName
java -cp out com.example.geometrydash.GameModelChecks
```

Elas verificam catálogo e seleção de fases, dados e imutabilidade dos mapas, salto, inversão de gravidade e colisões básicas.

A janela do jogo mede 900 × 450 pixels. Para jogar a versão alternativa sem instalar Java, abra `jogar.html` em um navegador.

## Controles

- **Lobby:** clique em uma fase disponível e em **JOGAR**; **Enter** inicia a fase selecionada.
- **Partida:** **Espaço** ou clique na área do jogo faz o personagem pular, quando está apoiado.
- **Derrota ou vitória:** use **JOGAR NOVAMENTE** ou **VOLTAR AO LOBBY**; **Espaço** ou **R** reinicia; **Esc** retorna ao lobby.

## Objetivo acadêmico

O jogo reúne conceitos de Java e Programação Orientada a Objetos em um projeto prático: separação de responsabilidades, composição entre objetos, hierarquia polimórfica de obstáculos, atualização da física por tempo decorrido e representação de estados e regras do jogo.
