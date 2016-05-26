(ns conway-life.core
  (:gen-class))

(def ^:constant alive 1)
(def ^:constant dead 0)

;;; (deftype Board (Vector of Vector of Integers))

(defn make-board
  "make-board: Integer -> Board
   Creates a 2 dimensional vector containing about 40% living cells, and about 60% dead ones."
   [limit]
  (for [y (range limit)]
    (for [x (range limit)]
      (if (> 0.4 (rand)) alive dead))))

(defn display-board 
  "display-board: Board -> IO
   Prints each row in the board, representing living cells with a filled square and dead ones with an empty square."
  [board]
  (println
   (clojure.string/join
    "\n"
    (map
     (fn [row]
       (clojure.string/join " " (map #(if (zero? %) "□" "■") row)))
     board))))

(defn get-square
  "get-square: Board, Integer, Integer -> Integer
   Takes the board and a set of coordinates, and returns the contents of those coordinates.
   If the item isn't in the board, it returns 0."
   [board x y]
  (nth (nth board y '()) x 0))

(defn neighbours
  "neighbours: Board, Integer, Integer -> List of Integers
   Finds the contents of each square directly adjacent to the one at the set of coordinates it is passed."
   [board x y]
  (for [my [-1 0 1]
        mx [-1 0 1]
        :when (not (and (zero? my) (zero? mx)))]
    (get-square board (+ mx x) (+ my y))))


(defn update-square 
  "update-square: Board, Integer, Integer -> Integer
   Determines whether a cell should die or continue to live, based on the status of its neighbours.
   If the cell has 3 neighbours, or it is alive and has 2 neighbours, it becomes alive. Otherwise, it's dead."
  [board x y]
  (let [neighbour-sum (apply + (neighbours board x y))]
    (if (or (= neighbour-sum 3)
            (and (= neighbour-sum 2)
                 (= (get-square board x y) alive)))
      alive
      dead)))

(defn update-board 
  "update-board: Board -> Board
   Takes in the current board state, and produces a new board where each square has been updated."
  [board]
  (for [y (range (count board))]
    (for [x (range (count (nth board y)))]
      (update-square board x y))))

(defn loop-state 
  "loop-state: Board -> IO
   The main loop of the game. Takes in the current state, prints it, then recurses with the updated state."
  [state]
  (display-board state)
  (println "Press any key to continue. Press Q to quit.")
  (if (= (clojure.string/lower-case (read-line)) "q")
    (println "Bye!")
    (loop-state (update-board state))))

(defn -main [limit-s & args]
  (let [limit (Integer/parseInt limit-s)]
    (if (> limit 50)
      (println "Let's not get crazy here, bud. Try something lower.")
      (loop-state (make-board limit)))))

