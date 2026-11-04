# Sudoku BR

Aplicativo de Sudoku para Android, em português, com tabuleiros 6x6, 9x9 e 12x12, quatro níveis de dificuldade, notas, desfazer, estatísticas e partidas salvas. Gratuito, com anúncios.

## Origem e licença

Este projeto é um **fork do [LibreSudoku](https://github.com/kaajjo/Libre-Sudoku)**, criado por kaajjo e colaboradores, e é distribuído sob a **GNU General Public License v3.0**. Veja o arquivo [LICENSE](LICENSE). O histórico de commits do projeto original foi mantido.

Como a GPL-3.0 exige, o código-fonte completo de cada versão publicada deste aplicativo fica disponível neste repositório, e qualquer pessoa pode estudá-lo, modificá-lo e redistribuí-lo nos termos da mesma licença.

### O que foi alterado em relação ao original

- Nome do aplicativo, identificador do pacote (`dev.vieira85lucas.sudokubr`) e numeração de versões.
- Tela "Sobre": removidos os dados de doação e os links de comunidade do autor original; adicionados o crédito ao projeto original, a política de privacidade e o aviso de licença.
- Tela de erro: removido o link para o canal do autor original.
- Textos da loja e documentação em português do Brasil.
- Inclusão de anúncios (Google AdMob) com tela de consentimento. *(em andamento)*

O pacote de código Kotlin (`com.kaajjo.libresudoku`) foi mantido de propósito, para facilitar a sincronização com o projeto original.

## Privacidade

A política de privacidade está em [docs/privacy-policy.md](docs/privacy-policy.md).

## Compilar

Requisitos: JDK 17 e Android SDK 35.

```bash
./gradlew :app:assembleDebug     # APK de debug
./gradlew :app:testDebugUnitTest # testes unitários
```

Para publicar na Play Store é preciso um keystore próprio de assinatura. Nunca coloque o keystore ou as senhas no repositório; o workflow em `.github/workflows` lê tudo de *secrets*.

## Sincronizar com o projeto original

```bash
git remote add upstream https://github.com/kaajjo/Libre-Sudoku.git
git fetch upstream
git merge upstream/main
```

## Créditos

- [LibreSudoku](https://github.com/kaajjo/Libre-Sudoku) — kaajjo e colaboradores (GPL-3.0)
- QQWing (versão modificada) — geração e resolução de puzzles
- Demais bibliotecas listadas em *Mais > Sobre > Licenças*
