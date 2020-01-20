import java.util.*;

public class Main {


    public static void main(String[] args) {



        Scanner scanner = new Scanner(System.in);
        int NumberOfTitans;
        int NumberOfBlessings;
        int NumberOfLands;
        boolean ggt;
        boolean lifeFromTheLoam;
        int NumberOfNonLands;
        System.out.println("Use Standard Double Titan list? (y/n)");
        if(scanner.next().matches("y")){
            NumberOfTitans=2;
            NumberOfBlessings=0;
            NumberOfLands= 34;
            ggt = true;
            lifeFromTheLoam = true;
            NumberOfNonLands=99-2-NumberOfLands-NumberOfTitans-NumberOfBlessings;
        } else {
            System.out.println("Use Standard One Titan one Gaea's Blessing list? (y/n)");
            if(scanner.next().matches("y")){
                NumberOfTitans=1;
                NumberOfBlessings=1;
                NumberOfLands= 34;
                ggt = true;
                lifeFromTheLoam = true;
                NumberOfNonLands=99-2-NumberOfLands-NumberOfTitans-NumberOfBlessings;
            } else {
                System.out.print("Number of Cards Total in your deck?");
                NumberOfNonLands = scanner.nextInt();

                System.out.print("Number of Titans in your build: ");
                 NumberOfTitans = scanner.nextInt();

                System.out.print("Number of Gaea's Blessing in your build: ");
                 NumberOfBlessings = scanner.nextInt();

                System.out.print("Number of Lands in your build (including Dakmor): ");
                 NumberOfLands = scanner.nextInt();

                System.out.print("Playing Golgary grave troll? (y/n)");
                 ggt = scanner.next().matches("y");

                System.out.print("Playing Life from the loam? (y/n)");
                 lifeFromTheLoam = scanner.next().matches("y");

                 NumberOfNonLands= NumberOfNonLands -  ((ggt ? 1 : 0)+ (lifeFromTheLoam ? 1 : 0)
                         + NumberOfLands + NumberOfTitans + NumberOfBlessings);
            }
        }
        System.out.println("How many Cards are you looking to sculpt?");
        int NumberOfLooksFor = scanner.nextInt();

        NumberOfNonLands-=NumberOfLooksFor;
        List<Integer> Decklist = Game.DecklistCustom(
                NumberOfTitans,
                NumberOfBlessings,
                NumberOfLands,
                NumberOfNonLands,
                NumberOfLooksFor,
                0,
                lifeFromTheLoam ? 1 : 0,
                ggt ? 1 : 0
        );

        System.out.println("How many tries you wanna try?");
        int NumberOfTries = scanner.nextInt();

        Game game = new Game();
        System.out.println("We wanna try seaching for "+NumberOfLooksFor+" relevant cards "+NumberOfTries+" times.");
        game.test(Decklist,NumberOfTries,NumberOfLooksFor);
        //game.play();
        //game.test();
        //game.testEverything5000Count();
    }


}
