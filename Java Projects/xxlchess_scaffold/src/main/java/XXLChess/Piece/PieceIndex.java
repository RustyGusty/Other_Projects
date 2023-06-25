package XXLChess.Piece;

/**
 * Allows using names of pieces instead of numbered indices to be clearer and easier to code in
 */
public enum PieceIndex{
    PAWN,
    KNIGHT,
    CAMEL,
    BISHOP,
    GENERAL,
    ROOK,
    ARCHBISHOP,
    CHANCELLOR,
    QUEEN,
    AMAZON,
    KING;

    /**
     * How many pieces are available, used to make the image array
     */
    public static final int size;
    static{
        size = values().length;
    }

    /**
     * Given a letter ch, returns the associated PieceIndex with that piece ignoring capitalization
     * (PAWN for p, QUEEN for q, CHANCELLOR for e, etc.)
     * @param ch - a char holding the letter to be checked against
     * @return null if ch is not a valid letter, the corresponding PieceIndex enum value otherwise
     */
    public static PieceIndex letterToIndex(char ch){
        ch = Character.toLowerCase(ch);
        switch(ch){
            case 'p':
                return PAWN;
            case 'n':
                return KNIGHT;
            case 'c':
                return CAMEL;
            case 'b':
                return BISHOP;
            case 'g':
                return GENERAL;
            case 'r':
                return ROOK;
            case 'h':
                return ARCHBISHOP;
            case 'e':
                return CHANCELLOR;
            case 'q':
                return QUEEN;
            case 'a':
                return AMAZON;
            case 'k':
                return KING;
            default:
                return null;
        }
    }
}