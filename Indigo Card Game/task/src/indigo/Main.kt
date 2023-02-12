package indigo

import kotlin.random.Random

class Deck {
    private val ranks = listOf(
        "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"
    )

    private val suits = listOf("♦", "♥", "♠", "♣")

    private val deck = mutableListOf<String>()

    init {
        formNewDeck()
    }

    private fun shuffle() {
        deck.shuffle()
    }

    private fun formNewDeck() {
        for (rank in ranks) {
            for (suit in suits) {
                deck.add("$rank$suit")
            }
        }
        deck.shuffle()
    }

    private fun resetDeck() {
        deck.clear()
        formNewDeck()
    }

    fun giveCards(cardsNum: Int): MutableList<String> {
        if (cardsNum > deck.size) {
            println("The remaining cards are insufficient to meet the request.")
            return deck
        }
        val subList = deck.subList(0, cardsNum)
        val copyList = mutableListOf<String>()
        copyList.addAll(subList)
        subList.clear()
        return copyList
    }

    fun cardsLeft(): Int = deck.size
    fun topCard(): String = deck.last()
}

class Game(
    val cardsOnTable: MutableList<String>,
    private val cardsPlayer: MutableList<String>,
    private val cardsPc: MutableList<String>
) {

    private val playerWonCards = mutableListOf<String>()
    private val pcWonCards = mutableListOf<String>()
    var lastWon: String = ""
    var firstMove: String = ""
    var playerPoints = 0
    var pcPoints = 0

    fun playerTurn(cardIndex: Int): Boolean {
        val card = cardsPlayer.removeAt(cardIndex)
        val top = topCard()
        cardsOnTable.add(card)
        if (cardsMatches(top, card)) {
            playerWonDesk()
            lastWon = "player"
            return true
        }
        return false
    }

    fun pcTurn(): Boolean {
        val index = chooseIndexBestMovePc()
        val card = cardsPc.removeAt(index)
        val top = topCard()
        cardsOnTable.add(card)
        if (cardsMatches(top, card)) {
            pcWonDesk()
            lastWon = "pc"
            return true
        }
        return false
    }

    private fun chooseIndexBestMovePc(): Int {
        if (cardsPc.size == 1) return 0
        if (tableSize() == 0) {
            return chooseBestIndexWhenEmptyTableOrNoCandidates()
        }
        val top = topCard()
        val suitCandidateIndexes = mutableListOf<Int>()
        val rankCandidateIndexes = mutableListOf<Int>()
        for (index in cardsPc.indices) {
            if (cardsPc[index].contains(top.first()))
                rankCandidateIndexes.add(index)
            if (cardsPc[index].contains(top.last()))
                suitCandidateIndexes.add(index)
        }
        if (suitCandidateIndexes.size > 1)
            return suitCandidateIndexes[Random.nextInt(suitCandidateIndexes.size)]
        if (rankCandidateIndexes.size > 1)
            return rankCandidateIndexes[Random.nextInt(rankCandidateIndexes.size)]
        if (suitCandidateIndexes.size == 1)
            return suitCandidateIndexes[0]
        if (rankCandidateIndexes.size == 1)
            return rankCandidateIndexes[0]
        return chooseBestIndexWhenEmptyTableOrNoCandidates()
    }

    private fun chooseBestIndexWhenEmptyTableOrNoCandidates(): Int {
        for (suit in listOf("♦", "♥", "♠", "♣")) {
            if (cardsPc.count { it.contains(suit) } > 1) {
                val indexes = mutableListOf<Int>()
                for (index in cardsPc.indices) {
                    if (cardsPc[index].contains(suit)) indexes.add(index)
                }
                return indexes[Random.nextInt(indexes.size)]
            }
        }
        for (rank in listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")) {
            if (cardsPc.count { it.contains(rank) } > 1) {
                val indexes = mutableListOf<Int>()
                for (index in cardsPc.indices) {
                    if (cardsPc[index].contains(rank)) indexes.add(index)
                }
                return indexes[Random.nextInt(indexes.size)]
            }
        }
        return Random.nextInt(cardsPc.size)
    }

    private fun cardsMatches(card1: String, card2: String): Boolean {
        return card1.first() == card2.first() || card1.last() == card2.last()
    }

    fun playersCardsToString(): String {
        val sBuilder = StringBuilder()
        for (i in cardsPlayer.indices) {
            sBuilder.append("${i + 1})${cardsPlayer[i]} ")
        }
        return sBuilder.toString()
    }

    fun pcCardsToString(): String {
        return cardsPc.joinToString(" ")
    }

    fun dealCards(playerNewHand: MutableList<String>, pcNewHand: MutableList<String>) {
        cardsPlayer.addAll(playerNewHand)
        cardsPc.addAll(pcNewHand)
    }

    fun playerWonDesk() {
        playerWonCards.addAll(cardsOnTable)
        var points = 0
        playerWonCards.forEach { if (it.matches("[1AJQK].+".toRegex())) points++ }
        playerPoints = points
        cardsOnTable.clear()
    }

    fun pcWonDesk() {
        pcWonCards.addAll(cardsOnTable)
        var points = 0
        pcWonCards.forEach { if (it.matches("[1AJQK].+".toRegex())) points++ }
        pcPoints = points
        cardsOnTable.clear()
    }

    fun pcPointsAndCards(): Pair<Int, Int> {
        return Pair(pcPoints, pcWonCards.size)
    }

    fun playerPointsAndCards(): Pair<Int, Int> {
        return Pair(playerPoints, playerWonCards.size)
    }

    fun playerSize(): Int = cardsPlayer.size
    fun tableSize(): Int = cardsOnTable.size
    fun topCard(): String = if (cardsOnTable.size == 0) " " else cardsOnTable.last()
    fun noCardOnHands(): Boolean = cardsPlayer.size == 0 && cardsPc.size == 0
    fun setFirst(playerFirst: Boolean) {
        firstMove = if (playerFirst) "player" else "pc"
    }

    fun finalThreePointsPlay() {
        when {
            pcWonCards.size > playerWonCards.size -> pcPoints += 3
            playerWonCards.size > pcWonCards.size -> playerPoints += 3
            else -> {
                if (firstMove == "player") playerPoints += 3
                else pcPoints += 3
            }
        }
    }

    fun pcLastWonCard(): String = pcWonCards.last()
}

val deck = Deck()
val game = Game(
    deck.giveCards(4),
    deck.giveCards(6),
    deck.giveCards(6)
)

fun main() {
    println("Indigo Card Game")
    var activePlayer = isPlayerFirst()
    game.setFirst(activePlayer)
    println("Initial cards on the table: ${game.cardsOnTable.joinToString(" ")}")
    while (true) {
        println()
        val gameGoOn = makeNextTurnOrExit(activePlayer)
        if (!gameGoOn) break
        if (noMoreCards()) {
            println()
            println(
                if (game.tableSize() == 0) "No cards on the table"
                else "${game.tableSize()} cards on the table, and the top card is ${game.topCard()}"
            )
            playRemainCards()
            outputScore()
            break
        }
        if (game.noCardOnHands()) {
            game.dealCards(
                deck.giveCards(6),
                deck.giveCards(6)
            )
        }
        activePlayer = !activePlayer
    }
    println("Game over")
}

fun playRemainCards() {
    if (game.lastWon == "") {
        if (game.firstMove == "player")
            game.playerWonDesk()
        else game.pcWonDesk()
    } else {
        if (game.lastWon == "player")
            game.playerWonDesk()
        else game.pcWonDesk()
    }
    game.finalThreePointsPlay()
}

private fun noMoreCards() = deck.cardsLeft() == 0 && game.noCardOnHands()

fun makeNextTurnOrExit(activePlayer: Boolean): Boolean {
    println(
        if (game.tableSize() == 0) "No cards on the table"
        else "${game.tableSize()} cards on the table, and the top card is ${game.topCard()}"
    )
    if (activePlayer) return playerTurnWithExitOption()
    else pcTurn()
    return true
}

fun pcTurn() {
    println(game.pcCardsToString())
    val isDeskWon = game.pcTurn()
    if (isDeskWon) {
        println("Computer plays ${game.pcLastWonCard()}")
        println("Computer wins cards")
        outputScore()
    } else println("Computer plays ${game.topCard()}")
}

fun playerTurnWithExitOption(): Boolean {
    println("Cards in hand: ${game.playersCardsToString()}")
    var cardNum = -1
    while (cardNum < 0) {
        println("Choose a card to play (1-${game.playerSize()}):")
        val choice = readln()
        if (choice == "exit") return false
        try {
            val intChoice = choice.toInt()
            if (intChoice < 1 || intChoice > game.playerSize()) continue
            cardNum = intChoice - 1
        } catch (e: NumberFormatException) {
            continue
        }
    }
    val isDeskWon = game.playerTurn(cardNum)
    if (isDeskWon) {
        println("Player wins cards")
        outputScore()
    }
    return true
}

fun outputScore() {
    println(
        """
        Score: Player ${game.playerPointsAndCards().first} - Computer ${game.pcPointsAndCards().first}
        Cards: Player ${game.playerPointsAndCards().second} - Computer ${game.pcPointsAndCards().second}
    """.trimIndent()
    )
}

private fun isPlayerFirst(): Boolean {
    while (true) {
        println("Play first?")
        return when (readln()) {
            "yes" -> true
            "no" -> false
            else -> continue
        }
    }
}




