package com.example.servertemplateforcardsupdate2122;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.*;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import socketfx.Constants;
import socketfx.FxSocketServer;
import socketfx.SocketListener;
public class HelloController implements Initializable {
    boolean areReady = false;
    boolean clientReady = false;
    boolean readyDiscard = true;
    boolean firstClickedImageForSwap = true;
    boolean secondClickedImageForSwap = false;
    boolean drawFromDiscard = true;  //initially you can draw from discard pile, but only once
    boolean drawFromDeck = true;
    boolean a;  //a through d are for server check 4 in row for draw from deck
    boolean b;
    boolean c;
    boolean d;
    boolean e;  //e through h are for server check 4 in row for draw from discard
    boolean f;
    boolean g;
    boolean h;
    boolean i;   // i through l are for client check 4 in row for draw from deck
    boolean j;
    boolean k;
    boolean l;
    boolean m;  // m through p are for client check 4 in row for draw from discard
    boolean n;
    boolean o;
    boolean p;
    @FXML
    private Button sendButton,ready, swap, enableDiscard;
    @FXML
    private TextField sendTextField;
    @FXML
    private Button connectButton;
    @FXML
    private TextField portTextField;
    @FXML
    private Label lblMessages, winLabel;
    @FXML
    private ImageView imgS0,imgS1,imgS2,imgS3,imgS4,imgS5,imgS6,imgS7,imgS8,imgS9,imgS10,imgS11,imgS12,imgS13,
            imgC0,imgC1,imgC2,imgC3,imgC4, imgC5,imgC6,imgC7,imgC8,imgC9, imgC10, imgC11, imgC12, imgC13, imgDiscard;

    @FXML
    Button deal,draw;
    FileInputStream back1,tempCard, tempCard2;
    Image imageBack;
    Image imageFront;
    Image imageFront2;
    List<Card> deck = new ArrayList<>();  //arraylist of entire deck
    Card discard;
    List<ImageView> hand1I = new ArrayList<>(); //arraylist of imageviews for front of cards
    List<ImageView> hand2I = new ArrayList<>();  //arraylist of imageviews for back of cards
    List<Card> hand1D = new ArrayList<>();  //server hand
    List<Card> hand2D = new ArrayList<>();  //client hand
    List<Card> discardPile = new ArrayList<>();

    private int numCardsInDeck;
    private int turn = 0;
    int serverImgClicked;
    int clientImgClicked;
    int serverImgClickedForSwap1;
    int serverImgClickedForSwap2;
    int clientImgClickedForSwap;
    int clientImgClickedForSwap2;



    int totalNumberOfThreeOfARow;
    int totalNumberOfThreeOfARowClient;
    int totalNumberOfThreeOfAKind;

    int clubsTotalThreeCombos;
    int heartsTotalThreeCombos;
    int spadesTotalThreeCombos;
    int diamondsTotalThreeCombos;
    int clubsTotalThreeCombos2;
    int heartsTotalThreeCombos2;
    int spadesTotalThreeCombos2;
    int diamondsTotalThreeCombos2;
    int clubsTotalThreeCombos3;
    int heartsTotalThreeCombos3;
    int spadesTotalThreeCombos3;
    int diamondsTotalThreeCombos3;
    int clubsTotalThreeCombos4;
    int heartsTotalThreeCombos4;
    int spadesTotalThreeCombos4;
    int diamondsTotalThreeCombos4;

    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private boolean isConnected;
    private int counter = 0;
    private String color;



    public enum ConnectionDisplayState {

        DISCONNECTED, WAITING, CONNECTED, AUTOCONNECTED, AUTOWAITING
    }

    private FxSocketServer socket;

    private void connect() {
        socket = new FxSocketServer(new FxSocketListener(),
                Integer.valueOf(portTextField.getText()),
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    private void displayState(ConnectionDisplayState state) {
//        switch (state) {
//            case DISCONNECTED:
//                connectButton.setDisable(false);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case WAITING:
//            case AUTOWAITING:
//                connectButton.setDisable(true);
//                sendButton.setDisable(true);
//                sendTextField.setDisable(true);
//                break;
//            case CONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//            case AUTOCONNECTED:
//                connectButton.setDisable(true);
//                sendButton.setDisable(false);
//                sendTextField.setDisable(false);
//                break;
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isConnected = false;
        displayState(ConnectionDisplayState.DISCONNECTED);




        Runtime.getRuntime().addShutdownHook(new ShutDownThread());

        /*
         * Uncomment to have autoConnect enabled at startup
         */
//        autoConnectCheckBox.setSelected(true);
//        displayState(ConnectionDisplayState.WAITING);
//        connect();
    }

    class ShutDownThread extends Thread {

        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                socket.shutdown();
            }
        }
    }
    @FXML
    private void handleConnectButton(ActionEvent event) {
        connectButton.setDisable(true);

        displayState(ConnectionDisplayState.WAITING);
        connect();
    }
    //****************************************************************
    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) throws FileNotFoundException {
            lblMessages.setText(line);
            if (line.equals("ready") && areReady){
                deal.setDisable(false);
                ready.setVisible(false);

            }else if(line.equals("ready")){
                clientReady=true;

            }else if(line.equals(("clientDrawCard"))){
                drawFromDeck = true;
                drawFromDiscard = true;
                clientDrawCard();
            }
            else if(line.equals("cdd")){
                drawFromDeck = true;
                drawFromDiscard = true;
                doClientDrawFromDiscard();
            }

            else if(line.startsWith("csc")){
                clientImgClickedForSwap = Integer.parseInt(line.substring(3,4));
                clientImgClickedForSwap2 = Integer.parseInt(line.substring(4));
                doClientSwap();
                //call swap method for client


            }

            else if(line.equals("client swapping cards")){
                System.out.println("client is swapping cards");
            }

            else if(line.startsWith("cic")){
                clientImgClicked =Integer.parseInt(line.substring(3));
                doClientDiscard();

            }
        }

        @Override
        public void onClosedStatus(boolean isClosed) {

        }
    }

    @FXML
    private void handleSendMessageButton(ActionEvent event) {
        if (!sendTextField.getText().equals("")) {
            socket.sendMessage(sendTextField.getText());
        }
    }
    @FXML
    private void handleReady(ActionEvent event) {
        areReady=true;
        socket.sendMessage("ready");
        if (clientReady){
            ready.setVisible(false);
            deal.setDisable(false);

        }else{
            ready.setDisable(true);
        }
    }

    @FXML
    private void handleSwap(ActionEvent event) {
        System.out.println(drawFromDeck + "server deck boolean");
        System.out.println(drawFromDiscard + "server discard boolean");
        readyDiscard = false;
//        firstClickedImageForSwap = false;
//        secondClickedImageForSwap = false;
        socket.sendMessage("server swapping cards");

        if (!readyDiscard){
            deal.setDisable(true);   //remember to enable buttons after swapping and set readyDis to true
            draw.setDisable(true);

        }else{
            //nothing
        }
    }

    @FXML
    private void handleEnableDiscard(ActionEvent event){
        readyDiscard = true;
        socket.sendMessage("server has finished swapping cards for now");   //configure client to recieve this
        if(readyDiscard){
            deal.setDisable(false);
            draw.setDisable(false);
        }else{
            //nothing as well
        }
    }

    public void handleServerImgClicked(MouseEvent mouseEvent) {
        if(readyDiscard){
            serverImgClicked = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
            doServerDiscard();
        }else{
            if(secondClickedImageForSwap){
                serverImgClickedForSwap2 = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
//
//                firstClickedImageForSwap = true;
                if(!firstClickedImageForSwap && secondClickedImageForSwap){
                    doServerSwap();
                }
                firstClickedImageForSwap = true;
                secondClickedImageForSwap = false;

            }else if(firstClickedImageForSwap){
                serverImgClickedForSwap1 = GridPane.getColumnIndex((ImageView) mouseEvent.getSource());
                firstClickedImageForSwap = false;
                secondClickedImageForSwap = true;
            }
        }
    }
    public void handleServerDiscardClicked(MouseEvent event) throws FileNotFoundException {
        if(drawFromDiscard){
            System.out.println("server draw from discard is true");
            if(turn ==0){
                turn = 1;
                drawFromDeck = false;
                doServerDrawFromDiscard();
                System.out.println(turn + "this persons turn now");
            }
        }
    }

//    public void doServerDiscard(){
//        discardPile.add(hand1D.remove(serverImgClicked));
//        try {
//            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
//            imageFront = new Image(tempCard);
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }
//        imgDiscard.setImage(imageFront);
//        sendDiscardPile();
//        printServerScreen();
//    }

    public void doServerDrawFromDiscard() throws FileNotFoundException {
        List<Integer> hearts = new ArrayList<>();
        List<Integer> diamonds = new ArrayList<>();
        List<Integer> clubs = new ArrayList<>();
        List<Integer> spades = new ArrayList<>();
        List<Integer> threeOfKindArraylist = new ArrayList<>();
        int numThreeOfAkind = 0;
        hand1D.add(discardPile.remove(discardPile.size()-1));
        socket.sendMessage("sdd");
        totalNumberOfThreeOfARow= 0;    //resets instance var so this updates every time

        try{
            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
            imageFront = new Image(tempCard);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        imgS13.setImage(imageFront);
        imgDiscard.setImage(new Image(new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath())));
        sendDiscardPile();
        printServerScreen();

        for(int i = 0; i < hand1D.size();i++){

            if(hand1D.get(i).getCardName().charAt(0) == 'C'){
                clubs.add(hand1D.get(i).getCardNumber());
            }else if(hand1D.get(i).getCardName().charAt(0) == 'H'){
                hearts.add(hand1D.get(i).getCardNumber());
            }else if(hand1D.get(i).getCardName().charAt(0) == 'S'){
                spades.add(hand1D.get(i).getCardNumber());
            }else{
                diamonds.add(hand1D.get(i).getCardNumber());
            }
        }
        Collections.sort(clubs);
        Collections.sort(hearts);
        Collections.sort(spades);
        Collections.sort(diamonds);
        if(clubs.size()>3){
            e = checkForFourInRow(clubs);

        }
        if(clubs.size()>2){
            clubsTotalThreeCombos2 = checkForThreeInRow(clubs);
            totalNumberOfThreeOfARow+= clubsTotalThreeCombos2;
        }
        if(hearts.size()>3 && !e){
            f = checkForFourInRow(hearts);

        }
        if(hearts.size()>2 && totalNumberOfThreeOfARow<3){
            heartsTotalThreeCombos2 = checkForThreeInRow(hearts);
            totalNumberOfThreeOfARow+= heartsTotalThreeCombos2;
        }
        if(spades.size()>3 && !f){
            g = checkForFourInRow(spades);

        }
        if(spades.size()>2 && totalNumberOfThreeOfARow<3){
            spadesTotalThreeCombos2 = checkForThreeInRow(spades);
            totalNumberOfThreeOfARow+= spadesTotalThreeCombos2;
        }
        if(diamonds.size()>3 && !g){
            h = checkForFourInRow(diamonds);

        }
        if(diamonds.size()>2 && totalNumberOfThreeOfARow<3){
            diamondsTotalThreeCombos2 = checkForThreeInRow(diamonds);
            totalNumberOfThreeOfARow+= diamondsTotalThreeCombos2;
        }

        threeOfKindArraylist.addAll(clubs);
        threeOfKindArraylist.addAll(hearts);
        threeOfKindArraylist.addAll(spades);
        threeOfKindArraylist.addAll(diamonds);
        Collections.sort(threeOfKindArraylist);
        numThreeOfAkind = checkForThreeOfAKind(threeOfKindArraylist);
        System.out.println(totalNumberOfThreeOfARow+ "total three of kind is this much");

        if(totalNumberOfThreeOfARow + numThreeOfAkind == 3 && (e || f || g || h)){
            doWinServer();
        }

    }


    public void doClientDrawFromDiscard() throws FileNotFoundException {
        totalNumberOfThreeOfARowClient= 0;
        List<Integer> clientHearts = new ArrayList<>();
        List<Integer> clientDiamonds = new ArrayList<>();
        List<Integer> clientClubs = new ArrayList<>();
        List<Integer> clientSpades = new ArrayList<>();
        List<Integer> clientThreeOfKindArraylist = new ArrayList<>();
        int clientNumThreeOfAKind = 0;
        turn = 0;
        hand2D.add(discardPile.remove(discardPile.size()-1));
        try{
            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
            imageFront = new Image(tempCard);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        socket.sendMessage("DFD"+ hand2D.get(hand2D.size()-1).getCardName());  //can't swap after this
        imgDiscard.setImage(new Image(new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath())));
        sendDiscardPile();
        printServerScreen();

        for(int i = 0; i < hand2D.size();i++){
            if(hand2D.get(i).getCardName().charAt(0) == 'C'){
                clientClubs.add(hand2D.get(i).getCardNumber());
            }else if(hand2D.get(i).getCardName().charAt(0) == 'H'){
                clientHearts.add(hand2D.get(i).getCardNumber());
            }else if(hand2D.get(i).getCardName().charAt(0) == 'S'){
                clientSpades.add(hand2D.get(i).getCardNumber());
            }else{
                clientDiamonds.add(hand2D.get(i).getCardNumber());
            }
        }

        Collections.sort(clientClubs);
        Collections.sort(clientHearts);
        Collections.sort(clientSpades);
        Collections.sort(clientDiamonds);

        if(clientClubs.size()>3){
            m = checkForFourInRow(clientClubs);

//            for(int a = 0; a < clientClubs.size();a++){
//                System.out.println(clientClubs.get(a) + " cliennt clubs after");
//            }
        }
        if(clientClubs.size()>2){
            clubsTotalThreeCombos4 = checkForThreeInRow(clientClubs);
            totalNumberOfThreeOfARowClient+= clubsTotalThreeCombos4;
        }
        if(clientHearts.size()>3 && !m){
            n = checkForFourInRow(clientHearts);

//            for(int a = 0; a < clientHearts.size();a++){
//                System.out.println(clientHearts.get(a) + "client hearts after");
//            }
        }
        if(clientHearts.size()>2 && totalNumberOfThreeOfARowClient< 3){
            heartsTotalThreeCombos4 = checkForThreeInRow(clientHearts);
            totalNumberOfThreeOfARowClient+= heartsTotalThreeCombos4;
        }
        if(clientSpades.size()>3 && !n){
            o = checkForFourInRow(clientSpades);

//            for(int a = 0; a < clientSpades.size();a++){
//                System.out.println(clientSpades.get(a) + "client spades after");
//            }
        }
        if(clientSpades.size()>2 && totalNumberOfThreeOfARowClient< 3){
            spadesTotalThreeCombos4 = checkForThreeInRow(clientSpades);
            totalNumberOfThreeOfARowClient+= spadesTotalThreeCombos4;
        }
        if(clientDiamonds.size()>3 && !o){
            p = checkForFourInRow(clientDiamonds);

            for(int a = 0; a < clientDiamonds.size();a++){
                System.out.println(clientDiamonds.get(a) + " client diamonds after");
            }
        }
        if(clientDiamonds.size()>2 && totalNumberOfThreeOfARowClient< 3){
            diamondsTotalThreeCombos4 = checkForThreeInRow(clientDiamonds);
            totalNumberOfThreeOfARowClient+= diamondsTotalThreeCombos4;
        }
        clientThreeOfKindArraylist.addAll(clientClubs);
        clientThreeOfKindArraylist.addAll(clientHearts);
        clientThreeOfKindArraylist.addAll(clientSpades);
        clientThreeOfKindArraylist.addAll(clientDiamonds);
        Collections.sort(clientThreeOfKindArraylist);
        clientNumThreeOfAKind = checkForThreeOfAKind(clientThreeOfKindArraylist);
        if(totalNumberOfThreeOfARow + clientNumThreeOfAKind == 3 && (m || n || o || p)){
            doWinClient();
        }
        System.out.println(totalNumberOfThreeOfARow+ "total three of kind is this much in draw from dis");
    }


    public void doClientDiscard(){
        discardPile.add(hand2D.remove(clientImgClicked));
        try {
            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
            imageFront = new Image(tempCard);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        imgDiscard.setImage(imageFront);
        sendDiscardPile();
        printServerScreen();
    }

    public void doServerSwap(){
        Collections.swap(hand1D, serverImgClickedForSwap1, serverImgClickedForSwap2);  //swap in server deck
        printServerScreen();
    }

    public void doClientSwap(){
        Collections.swap(hand2D, clientImgClickedForSwap, clientImgClickedForSwap2);  //swap in client deck
        printServerScreen();
    }

    @FXML
    private void handleDeal(ActionEvent event){
        hand1I.add(imgS0);
        hand1I.add(imgS1);
        hand1I.add(imgS2);
        hand1I.add(imgS3);
        hand1I.add(imgS4);
        hand1I.add(imgS5);
        hand1I.add(imgS6);
        hand1I.add(imgS7);
        hand1I.add(imgS8);
        hand1I.add(imgS9);
        hand1I.add(imgS10);
        hand1I.add(imgS11);
        hand1I.add(imgS12);
        hand1I.add(imgS13);
        //add img S13 and C13 only when click on discard pile to draw or main deck of cards
        hand2I.add(imgC0);
        hand2I.add(imgC1);
        hand2I.add(imgC2);
        hand2I.add(imgC3);
        hand2I.add(imgC4);
        hand2I.add(imgC5);
        hand2I.add(imgC6);
        hand2I.add(imgC7);
        hand2I.add(imgC8);
        hand2I.add(imgC9);
        hand2I.add(imgC10);
        hand2I.add(imgC11);
        hand2I.add(imgC12);
        hand2I.add(imgC13);
        //add img S13 and C13 only when click on discard pile to draw or main deck of cards

        imgC0.setImage(imageBack);
        imgC1.setImage(imageBack);
        imgC2.setImage(imageBack);
        imgC3.setImage(imageBack);
        imgC4.setImage(imageBack);
        imgC5.setImage(imageBack);
        imgC6.setImage(imageBack);
        imgC7.setImage(imageBack);
        imgC8.setImage(imageBack);
        imgC9.setImage(imageBack);
        imgC10.setImage(imageBack);
        imgC11.setImage(imageBack);
        imgC12.setImage(imageBack);

        //only set image once card is clicked on to draw from deck in other method
        deck.clear();
        //populate deck
        for(int i = 1;i<14;i++)
        {
            deck.add(new Card("C" +Integer.toString(i+1)));
            deck.add(new Card("S"+Integer.toString(i+1)));
            deck.add(new Card("H"+Integer.toString(i+1)));
            deck.add(new Card("D"+Integer.toString(i+1)));
        }
        //deal each hand
        hand2D.clear();
        hand1D.clear();
        //deal server hand
        numCardsInDeck = 52;
        System.out.println("Server hand");
        for(int i = 0;i<13;i++)
        {
            int y = (int)(Math.random()*(52-i));
            try {
                tempCard = new FileInputStream(deck.get(y).getCardPath());
                imageFront = new Image(tempCard);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            hand1I.get(i).setImage(imageFront);
            System.out.println(deck.get(y).getCardPath());
            hand1D.add(deck.remove(y));
            numCardsInDeck--;

        }
        //deal client hand
        System.out.println("Client hand");
        for(int i = 0;i<13;i++)
        {
            int y = (int)(Math.random()*(39-i));
            System.out.println(deck.get(y).getCardPath());

            hand2D.add(deck.remove(y));
            numCardsInDeck--;

        }
        socket.sendMessage("dealt");
        sendHandClient();
        lblMessages.setText("server draw a card");
        turn =0;
        //deal discard
//        discardPile.add(deck.get((int)(Math.random()*numCardsInDeck)));
//        numCardsInDeck--;
//        try {
//            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
//            imageFront = new Image(tempCard);
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }
//        draw.setDisable(false);
//        imgDiscard.setImage(imageFront);
//        sendDiscardPile();
    }
    public void handleDraw(){
        if(drawFromDeck){
            if (turn ==0){
                List<Integer> hearts = new ArrayList<>();
                List<Integer> diamonds = new ArrayList<>();
                List<Integer> clubs = new ArrayList<>();
                List<Integer> spades = new ArrayList<>();
                List<Integer> threeOfKindArraylist = new ArrayList<>(); //arraylist for 3 of kind combines
                //suits arraylists
                int numThreeOfAKind = 0;
                drawFromDiscard = false;
                hand1D.add(deck.get((int)(Math.random()*numCardsInDeck)));
                drawFromDiscard = false;  //only allows draw from discard when not drawing from deck
                numCardsInDeck--;
                printServerScreen();
                turn =1;
                socket.sendMessage("serverDrawCard");
                draw.setDisable(true);
                totalNumberOfThreeOfARow= 0;    //resets instance var so this updates every time
                for(int i = 0; i < hand1D.size();i++){

                    if(hand1D.get(i).getCardName().charAt(0) == 'C'){
                        clubs.add(hand1D.get(i).getCardNumber());
                    }else if(hand1D.get(i).getCardName().charAt(0) == 'H'){
                        hearts.add(hand1D.get(i).getCardNumber());
                    }else if(hand1D.get(i).getCardName().charAt(0) == 'S'){
                        spades.add(hand1D.get(i).getCardNumber());
                    }else{
                        diamonds.add(hand1D.get(i).getCardNumber());
                    }
                }

                // THIS SECTION ABOVE BASICALLY CREATES 4 SEPARATE ARRAYLISTS BASED ON SUITS, ONLY ADDS
                //CARD NUMBER, AND NOT CARD SUIT IN THE ARRAYLIST

                Collections.sort(clubs);
                Collections.sort(hearts);
                Collections.sort(spades);
                Collections.sort(diamonds);

                for(int j = 0; j < clubs.size();j++){
//                    System.out.println(clubs.get(j) + "clubs arraylist before");
                }

                for(int z = 0; z < hearts.size();z++){
//                    System.out.println(hearts.get(z) + "hearts arraylist before");
                }

                for(int a = 0; a < spades.size();a++){
//                    System.out.println(spades.get(a) + "spades arraylist before");
                }

                for(int b = 0; b < diamonds.size();b++){
//                    System.out.println(diamonds.get(b) + "diamonds arraylist before");
                }

                //ABOVE SECTIONS SORT EACH SUIT ARRAYLIST TO MAKE IS EASIER TO CHECK FOR COMBOS

                if(clubs.size()>3){
                    a = checkForFourInRow(clubs);

//                    for(int a = 0; a < clubs.size();a++){
//                        System.out.println(clubs.get(a) + "clubs after");
//                    }
                }

                if(clubs.size()>2){
                    clubsTotalThreeCombos = checkForThreeInRow(clubs);
                    totalNumberOfThreeOfARow+= clubsTotalThreeCombos;
                }

                if(hearts.size()>3 && !a){
                    b = checkForFourInRow(hearts);

//                    for(int a = 0; a < hearts.size();a++){
//                        System.out.println(hearts.get(a) + "hearts after");
//                    }
                }

                if(hearts.size()>2 && totalNumberOfThreeOfARow<3){
                    heartsTotalThreeCombos = checkForThreeInRow(hearts);
                    totalNumberOfThreeOfARow+= heartsTotalThreeCombos;
                }

                if(spades.size()>3 && !b){
                    c = checkForFourInRow(spades);

//                    for(int a = 0; a < spades.size();a++){
//                        System.out.println(spades.get(a) + "spades after");
//                    }
                }

                if(spades.size()>2 && totalNumberOfThreeOfARow<3){
                    spadesTotalThreeCombos = checkForThreeInRow(spades);
                    totalNumberOfThreeOfARow+= spadesTotalThreeCombos;
                }

                if(diamonds.size()>3 && !c){
                    d = checkForFourInRow(diamonds);

//                    for(int a = 0; a < diamonds.size();a++){
//                        System.out.println(diamonds.get(a) + "diamonds after");
//                    }
                }
                if(diamonds.size()>2 && totalNumberOfThreeOfARow<3){
                    diamondsTotalThreeCombos = checkForThreeInRow(diamonds);
                    totalNumberOfThreeOfARow+= diamondsTotalThreeCombos;
                }
                System.out.println(totalNumberOfThreeOfARow+ "total three of row is this much");

                System.out.println(clubs.size() + "clubs size before combo");
                System.out.println(hearts.size() + "hearts size before combo");
                System.out.println(spades.size() + "spades size before combo");
                System.out.println(diamonds.size() + "diamonds size before combo");
                threeOfKindArraylist.addAll(clubs);
                threeOfKindArraylist.addAll(hearts);
                threeOfKindArraylist.addAll(spades);
                threeOfKindArraylist.addAll(diamonds);
                Collections.sort(threeOfKindArraylist);   //sorts three of kind arraylist = easy to check
                //three of a kind
                numThreeOfAKind = checkForThreeOfAKind(threeOfKindArraylist);
                System.out.println(numThreeOfAKind + "total number of three of a kind");


                for(int i = 0; i < threeOfKindArraylist.size(); i++){
                    System.out.println(threeOfKindArraylist.get(i) + " combined arraylist elements");
                }

                if(totalNumberOfThreeOfARow + numThreeOfAKind == 3 && (a || b || c ||d)){
                    doWinServer();
                }

            }else{
                System.out.println("clients turn");
                System.out.println(turn + "this persons turn now from else block");
            }
        }

    }

    public void doWinServer(){
        draw.setDisable(true);
        deal.setDisable(true);
        swap.setDisable(true);
        enableDiscard.setDisable(true);
        winLabel.setText("YOU HAVE WON, CONGRATULATIONS");
        socket.sendMessage("YOU HAVE LOST. YOU SUCK AT INDIAN RUMMY");
    }


    public boolean checkForFourInRow(List<Integer> suits){
        int checkFourCounter = 0;
        int fourInRowFound = 0;  //maybe put as instance so can check for calling win
        for(int i = 0; i < suits.size()-3;i++){
            for(int j = i; j <= i + 2;j++){
                if(suits.get(j) + 1 == suits.get(j+1)){
                    checkFourCounter++;
                }else{
                    checkFourCounter = 0;    //this line is what i just added, should reset every time
                    break;                        ///combination of 4 does not continue
                }
                if(checkFourCounter == 3){
                    for(int x = 0; x < suits.size();x++){
                        System.out.println(suits.get(x) + "value of arraylist in method");
                    }
                    System.out.println(checkFourCounter + "Counter");
                    System.out.println(j + "jval");
                    for(int k = 0; k <4; k++){
                        suits.remove(j-2);
                    }
                    for(int f = 0; f < suits.size();f++){
                        System.out.println(suits.get(f) + "arraylist after deletion");
                    }
                    fourInRowFound = 1;
                }
            }
        }
        return fourInRowFound == 1;
    }

    public int checkForThreeInRow(List<Integer> suits){
        int checkThreeCounter = 0;
        int numberOfThreeOfAKind = 0;
        for(int i = 0; i < suits.size();i++){
            System.out.println(suits.get(i) + "elements of suits in 3()");
        }

        for(int i = 0; i < suits.size()-2;i++){
            for(int j = i; j <= i+1;j++){
                if(suits.get(j) + 1 == suits.get(j+1)){
                    checkThreeCounter++;
//                    System.out.println("print happens here");
                }else{
                    checkThreeCounter = 0;
                    break;
                }
                if(checkThreeCounter == 2){
                    numberOfThreeOfAKind++;  //this line might be wrong, but still worth checking
                    for(int k = 0; k<3; k++){
                        suits.remove(j-1);
                    }

                    for(int f = 0; f < suits.size();f++){
                        System.out.println(suits.get(f) + "arraylist after deletion");
                    }

                }

            }
        }
        System.out.println(numberOfThreeOfAKind + "  this is how many three of a row there are");
        return numberOfThreeOfAKind;  //actually refers to three in a row, just named var wrong
    }

    public int checkForThreeOfAKind(List<Integer> suits){
        int checkThreeKindCounter = 0;
        int numberOfThreeOfKind = 0;
        for(int i = 0; i < suits.size() -2 ;i++){
            for(int j = i; j <= i+1; j++){
                if(Objects.equals(suits.get(j), suits.get(j + 1))){  //check if they are equal
                    checkThreeKindCounter++;
                }else{
                    checkThreeKindCounter = 0;
                    break;
                }
                if(checkThreeKindCounter == 2){
                    numberOfThreeOfKind++;
//                    for(int k = 0; k <3; k++){
//                        suits.remove(j-1);
//                    }
                }
            }
        }
        System.out.println(numberOfThreeOfKind + "this is how many three of a kind there are");
        return numberOfThreeOfKind;
    }

    public void clientDrawCard(){
        totalNumberOfThreeOfARowClient= 0;
        List<Integer> clientHearts = new ArrayList<>();
        List<Integer> clientDiamonds = new ArrayList<>();
        List<Integer> clientClubs = new ArrayList<>();
        List<Integer> clientSpades = new ArrayList<>();
        List<Integer> clientThreeOfKindArraylist = new ArrayList<>();
        int clientNumThreeOfAKind = 0;
        hand2D.add(deck.get((int)(Math.random()*numCardsInDeck)));
        numCardsInDeck--;
        turn =0;
        draw.setDisable(false);
        printServerScreen();
        sendHandClient();

        for(int i = 0; i < hand2D.size();i++){
            if(hand2D.get(i).getCardName().charAt(0) == 'C'){
                clientClubs.add(hand2D.get(i).getCardNumber());
            }else if(hand2D.get(i).getCardName().charAt(0) == 'H'){
                clientHearts.add(hand2D.get(i).getCardNumber());
            }else if(hand2D.get(i).getCardName().charAt(0) == 'S'){
                clientSpades.add(hand2D.get(i).getCardNumber());
            }else{
                clientDiamonds.add(hand2D.get(i).getCardNumber());
            }
        }

        Collections.sort(clientClubs);
        Collections.sort(clientHearts);
        Collections.sort(clientSpades);
        Collections.sort(clientDiamonds);


        if(clientClubs.size()>3){
            i = checkForFourInRow(clientClubs);

//            for(int a = 0; a < clientClubs.size();a++){
//                System.out.println(clientClubs.get(a) + " cliennt clubs after");
//            }
        }
        if(clientClubs.size()>2){
            clubsTotalThreeCombos3 = checkForThreeInRow(clientClubs);
            totalNumberOfThreeOfARowClient+= clubsTotalThreeCombos3;
        }

        if(clientHearts.size()>3 && !i){
            j = checkForFourInRow(clientHearts);

//            for(int a = 0; a < clientHearts.size();a++){
//                System.out.println(clientHearts.get(a) + "client hearts after");
//            }
        }
        if(clientHearts.size()>2 && totalNumberOfThreeOfARowClient< 3){
            heartsTotalThreeCombos3 = checkForThreeInRow(clientHearts);
            totalNumberOfThreeOfARowClient+= heartsTotalThreeCombos3;
        }
        if(clientSpades.size()>3 && !j){
            k = checkForFourInRow(clientSpades);

//            for(int a = 0; a < clientSpades.size();a++){
//                System.out.println(clientSpades.get(a) + "client spades after");
//            }
        }
        if(clientSpades.size()>2 && totalNumberOfThreeOfARowClient< 3){
            spadesTotalThreeCombos3 = checkForThreeInRow(clientSpades);
            totalNumberOfThreeOfARowClient+= spadesTotalThreeCombos3;
        }
        if(clientDiamonds.size()>3 && !k){
            l = checkForFourInRow(clientDiamonds);

//            for(int a = 0; a < clientDiamonds.size();a++){
//                System.out.println(clientDiamonds.get(a) + " client diamonds after");
//            }
        }
        if(clientDiamonds.size()>2 && totalNumberOfThreeOfARowClient< 3){
            diamondsTotalThreeCombos3 = checkForThreeInRow(clientDiamonds);
            totalNumberOfThreeOfARowClient+= diamondsTotalThreeCombos3;
        }
        System.out.println(totalNumberOfThreeOfARowClient+ "client total 3 of kind");
        clientThreeOfKindArraylist.addAll(clientClubs);
        clientThreeOfKindArraylist.addAll(clientHearts);
        clientThreeOfKindArraylist.addAll(clientSpades);
        clientThreeOfKindArraylist.addAll(clientDiamonds);
        Collections.sort(clientThreeOfKindArraylist);
        clientNumThreeOfAKind = checkForThreeOfAKind(clientThreeOfKindArraylist);
        System.out.println(clientNumThreeOfAKind + "total number of three of a kind");

        if(totalNumberOfThreeOfARow + clientNumThreeOfAKind == 3 && (i || j || k ||l)){
            winLabel.setText("YOU HAVE LOST");
            doWinClient();
        }
    }

    public void doWinClient(){
//        draw.setDisable(true);
//        deal.setDisable(true);
//        swap.setDisable(true);
//        enableDiscard.setDisable(true);
//        winLabel.setText("YOU HAVE WON, CONGRATULATIONS");
        socket.sendMessage("CWIC");
    }

    public void printServerScreen(){
        for (ImageView x: hand1I){
            x.setImage(null);
        }
        for (ImageView x: hand2I){
            x.setImage(null);
        }
        System.out.println(hand1D.size() + "size of hand 1D");
        for (int i = 0;i<hand1D.size();i++){
            try {
                tempCard = new FileInputStream(hand1D.get(i).getCardPath());
                imageFront = new Image(tempCard);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            hand1I.get(i).setImage(imageFront);
        }
        for(int i =0;i<hand2D.size();i++){
            hand2I.get(i).setImage(imageBack);
        }
        sendHandClient();

    }




    public void sendHandClient(){
        socket.sendMessage("cCardStart");
        for (Card x: hand2D){

//            temp+="," + x.getCardName();
            socket.sendMessage("cCards"+ x.getCardName());
        }
        socket.sendMessage("sCardNum"+hand1D.size());

    }

    public void sendDiscardPile(){
        socket.sendMessage("dis"+discardPile.get(discardPile.size()-1).getCardName());
    }


    public void doServerDiscard(){
        discardPile.add(hand1D.remove(serverImgClicked));
        try {
            tempCard = new FileInputStream(discardPile.get(discardPile.size()-1).getCardPath());
            imageFront = new Image(tempCard);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        imgDiscard.setImage(imageFront);
        sendDiscardPile();
        printServerScreen();
    }

    public HelloController(){
        try {
            back1 = new FileInputStream("src/main/resources/Images/BACK-1.jpg");
            imageBack = new Image(back1);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }


}
