import java.util.*;

public class Game {
    // 0: useless non-land-card
    //1: Titan shufflers
    //2: gaes blessing shufflers we playing two titans
    //integers.add(2);
    //5: life from the loam, our dredge 3 boy
    //6: GGT, out dredge 7 boy
    //10: land
    //11: dakmor, our boy
    //42: card we look for in our sculpt
    //43 card we look for but its a land
    public List<Integer> Deck;
    public List<Integer> Hand;
    public List<Integer> Graveyard;
    // 0: gitrog draw trigger
    // 1: graveyard shuffle
    // 10: execute cleanup
    public Deque<Integer> DerStack;
    public List<Integer> WaitingForStack;
    public int searching;
    //duh

    public void shuffle() {
        shufflecount++;
        Collections.shuffle(Deck);
    }

    public long shufflecount;
    public long iterationCount;

    public Integer passPriority() {
        return (DerStack.size() == 0) ? 10 : DerStack.pop();
    }


    public void test(List<Integer> Decklist, int tries, int searching) {
        long startTime = System.nanoTime();
        int fails = 0;
        long shuffleTotal = 0;
        long iterationTotal = 0;
        this.searching = searching;
        iterationCount = 0;
        shufflecount = 0;
        for (int i = 0; i < tries; i++) {
            try {
                play(Decklist);
                shuffleTotal += shufflecount;
                iterationTotal +=  iterationCount;
            } catch (IllegalStateException e) {
                shuffleTotal += shufflecount;
                iterationTotal += iterationCount;
                fails++;
                System.out.println(fails +" "+ e.getLocalizedMessage() );

            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("fails: " + fails + " out of " + tries + " (" + ((float) fails / (float) tries * 100.0) + "%)");
        System.out.println("Average Shuffle per game: " + (shuffleTotal / tries) + " total: " + shuffleTotal);
        System.out.println("Average dakmor Dredges per game: " + (iterationTotal / tries) + " total: " + iterationTotal);
        System.out.println("Duration: " + duration / 1000000000.0);
    }

    private void play(List<Integer> Decklist) {
        StartSetUp(Decklist);
        doYourThing();
    }

    private void StartSetUp(List<Integer> Decklist){
        Deck = new ArrayList<>();
        Deck.addAll(Decklist);
        Hand = new ArrayList<>();
        Graveyard = new ArrayList<>();
        DerStack = new ArrayDeque<>();
        WaitingForStack = new ArrayList<>();
        if(Deck.remove((Integer) 11)==false){
            lose("Starting Deck did not contain Dakmor");
        };
        Hand.add(11);
        shuffle();
        shufflecount = 0;
        iterationCount = 0;
        draw(7);
    }

    public void statusReport() {
        //System.out.print("Cards in hand("+Hand.size()+"): ");
        for (int i : Hand
        ) {
            //System.out.print(i + ", ");
        }
        //System.out.println();
        //System.out.print("stuff on Stack("+DerStack.size()+"): ");
        for (int i : DerStack
        ) {
            if (i == 0) {
                //System.out.print("draw, ");
            }
            if (i == 1) {
                //System.out.print("shuffle, ");
            }
        }
        //System.out.println();
        //System.out.println("Size of Graveyard: "+Graveyard.size()+"  Size of Deck: "+ Deck.size()+"  "+
        //        "Cards in Total: "+(Graveyard.size()+Deck.size()+Hand.size()));
    }

    public void doYourThing() {
        while (true) {

            //statusReport();

            int thingToDo = passPriority();
            //statusReport();
            switch (thingToDo) {
                case 0:
                    handleDraw();
                    break;
                case 1:
                    Deck.addAll(Graveyard);
                    Graveyard.clear();
                    shuffle();
                    break;
                case 10:
                    handleDiscards();
                    break;
            }

            if (weWon()) {
                //System.out.println("We got there!");
                break;
            }

            handleDaStack();

        }

        if (weWon()) {
            //System.out.println("shufflecount: "+shufflecount);
        } else {
            System.out.println("we did not get there, sadly");
        }

    }

    private boolean weWon() {
        return (searching <= (Collections.frequency(Hand, (Integer) 42)
                + Collections.frequency(Hand, (Integer) 43)));
    }

    private void handleDaStack() {
        //todo: make smarter so we dont die to empty library that often
        if (WaitingForStack.size() == 1) {
            DerStack.push(WaitingForStack.remove(0));
        }
        if (WaitingForStack.size() == 2) {
            if (WaitingForStack.contains(0) && WaitingForStack.contains(1)) {
                DerStack.push(1);
                DerStack.push(0);
                WaitingForStack.clear();
            } else {
                DerStack.push(WaitingForStack.remove(0));
                DerStack.push(WaitingForStack.remove(0));
            }
        }

    }

    private void handleDiscards() {
        if (Hand.size() < 8) {
            lose("not enough cards in hand");
        } else if (Hand.size() == 8) {
            //System.out.println("-Discarding one card: "+whichCardToDiscard()+".");
            discard(whichCardToDiscard());
        } else if (Hand.size() == 9) {
            //System.out.println("-Discarding two cards.");
            discard(whichCardsToDiscards());
        } else
            throw new IllegalArgumentException("Number of cards in hand is too damn high");
    }

    private int whichCardToDiscard() {
        if (Hand.size() == 8) {
            //always discard dakmor
            if (Hand.contains(11)) {
                return 11;
            }
            //System.out.println("dont have Dakmor anymore, close to RIP");
            if (Hand.contains(10)) {
                return 10;
            } else {
                //System.out.println("dont have lands to discard, RIP");
                lose("No Lands in 8-card Hand, we stuck");
                return Hand.get(0);
            }
        }
        throw new IllegalArgumentException("started this despite not 8 cards in hand");
    }


    private List<Integer> whichCardsToDiscards() {
        assert Hand.size() == 9;

        List<Integer> cardsToDiscard = new ArrayList<>();

        if (Hand.contains(11)) {
            cardsToDiscard.add(11);
            int[] discardPriorityOder = new int[]{1, 2, 5, 6, 0, 10, 42, 43};
            for (int i : discardPriorityOder) {
                if (Hand.contains(i)) {
                    cardsToDiscard.add(i);
                    return (cardsToDiscard);
                }
            }
            lose("dont know how but we have weird cards in hand");
        } else if (Hand.contains(10)) {
            cardsToDiscard.add(10);
            int[] discardPriorityOder = new int[]{1, 2, 5, 6, 10, 42, 43};
            for (int i : discardPriorityOder) {
                if (Hand.contains(i)) {
                    cardsToDiscard.add(i);
                    return (cardsToDiscard);
                }
            }
            lose("dont know how but we have weird cards in hand");
        } else {
            //dont have lands, we are stuck. Just discard anything really
            lose("no more lands to discard, sadly");
        }
        throw new IllegalStateException("how did we end up HERE?");
    }

    private void handleDraw() {
        if (Graveyard.contains(11)) {
            Graveyard.remove((Integer) 11);
            Hand.add(11);
            mill(2);
        } else {
            draw();
        }
    }

    private void discard(Integer card) {
        if (card == 1) {
            WaitingForStack.add(1);
        }
        if (card == 11 || card == 10 || card == 43) {
            WaitingForStack.add(0);
        }
        if (Hand.remove(card) == false) {
            throw new IllegalArgumentException("Hand did not contain the card " + card);
        }
        Graveyard.add(card);
    }

    private void discard(List<Integer> cards) {
        if (cards.contains(1)) {
            WaitingForStack.add(1);
        }
        if (cards.contains(11) || cards.contains(10) || cards.contains(43)) {
            WaitingForStack.add(0);
        }
        for (Integer card : cards) {
            if (Hand.remove(card) == false) {
                throw new IllegalArgumentException("Hand did not contain the card " + card + " of the cards to discard");
            }
        }
        Graveyard.addAll(cards);
    }

    private void draw() {
        draw(1);
    }

    private void draw(int anzahl) {
        if (Deck.size() == 0) {
            lose("Drawing from empty Deck");
            return;
        }
        for (int i = 0; i < anzahl; i++) {
            Hand.add(Deck.remove(0));
        }
    }

    private void mill(int anzahl) {
        iterationCount++;
        boolean milledALand = false;
        if (Deck.size() < anzahl) {
            lose("tried milling too many cards");
            return;
        }
        for (int i = 0; i < anzahl; i++) {
            int card = Deck.remove(0);
            // if its a titan, shuffle trigger
            if (card == 1 || card == 2) {
                WaitingForStack.add(1);
            }
            //if its a land gitrog trigger, but only once for a mill
            if (card == 11 || card == 10 || card == 43) {
                milledALand = true;
            }
            Graveyard.add(card);
        }
        // if we milled at least one land we put a draw trigger on the waiting stack
        if (milledALand) {
            WaitingForStack.add(0);
        }
    }

    private void lose() {
        //System.out.println("Lost. No reason given.");
        statusReport();
        throw new IllegalStateException("Lost. No reason given.");
    }

    private void lose(String reason) {
        //System.out.println("Lost. Reason: " + reason);
        statusReport();
        throw new IllegalStateException("Lost. Reason: " + reason);
    }

    public static List<Integer> DecklistCustom(
            int TitanNumber,
            int GaesBlessingNumber,
            int Lands,
            int NonLands,
            int searchingForNonLands,
            int searchingForLands,
            int ggt,
            int loam
            ) {
        List<Integer> Decklist = new ArrayList<>();
        for (int i = 0; i < TitanNumber; i++) {
            Decklist.add(1);
        }
        for (int i = 0; i <  GaesBlessingNumber ; i++) {
            Decklist.add(  2 );
        }
        for (int i = 0; i <  Lands -1; i++) {
            Decklist.add( 10  );
        }
        //dakmor is always there for us. that is why we add Lands -1
        Decklist.add(11);
        for (int i = 0; i <  NonLands ; i++) {
            Decklist.add( 0  );
        }
        for (int i = 0; i <  searchingForLands ; i++) {
            Decklist.add(  43 );
        }
        for (int i = 0; i <  searchingForNonLands ; i++) {
            Decklist.add( 42  );
        }
        for (int i = 0; i <  ggt ; i++) {
            Decklist.add( 6  );
        }
        for (int i = 0; i <  loam ; i++) {
            Decklist.add( 5  );
        }

        if(Decklist.size()!=99){
            System.out.println("Warning: Decksize not 99, its "+ Decklist.size());
        }
        return Decklist;
    }

    private List<Integer> getDecklistStandartGitrog() {
        List<Integer> integers = new ArrayList<>();
        //add some cards
        // 0: useless non-land-card
        for (int i = 0; i < (61 - searching); i++) {
            integers.add(0);
        }

        //1: Titan shufflers
        integers.add(1);
        integers.add(1);

        //2: gaes blessing shufflers we playing two titans
        //integers.add(2);

        //5: life from the loam, our dredge 3 boy
        integers.add(5);

        //6: GGT, out dredge 7 boy
        integers.add(6);


        //10: lands
        for (int i = 0; i < 33; i++) {
            integers.add(10);
        }

        //11: dakmor, our boy
        integers.add(11);

        //42: card we look for in our sculpt
        for (int i = 0; i < searching; i++) {
            integers.add(42);
        }
        //43 card we look for but its a land
        for (int i = 0; i < 0; i++) {
            integers.add(43);
        }

        return integers;
    }
}
