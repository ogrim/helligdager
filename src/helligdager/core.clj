(ns helligdager.core
  (:require [clj-time.core :as time]
            [clj-time.local :as l])
  (:use [clojure.math.numeric-tower])
  (:import [org.joda.time ReadableDateTime]))

(def ^:private ekspanderte-helligdager (atom nil))

(defn- finn-påskedag [år]
  (let [a (rem år 19)
        b (floor (/ år 100))
        c (rem år 100)
        d (floor (/ b 4))
        e (rem b 4)
        f (floor (/ (+ b 8) 25))
        g (floor (/ (+ (- b f) 1) 3))
        h (rem (+ (- (- (+ (* 19 a) b) d) g) 15) 30)
        i (floor (/ c 4))
        k (rem c 4)
        L (rem (- (- (+ (+ 32 (* 2 e)) (* 2 i)) h) k) 7)
        m (floor (/ (+ (+ a (* 11 h)) (* 22 L)) 451))
        måned (floor (/ (+ (- (+ h L) (* 7 m)) 114) 31))
        dag (+ (rem (+ (- (+ h L) (* 7 m)) 114) 31) 1)]
    (time/date-time år måned dag)))

(defn- helligdager [år]
  (let [første-påskedag (finn-påskedag år)]
    [{:navn "Nyttårsdag" :dt (time/date-time år 1 1)}
     {:navn "Arbeidernes internasjonale kampdag" :dt (time/date-time år 5 1)}
     {:navn "Nasjonaldag" :dt (time/date-time år 5 17)}
     {:navn "Første Juledag" :dt (time/date-time år 12 25)}
     {:navn "Andre Juledag" :dt (time/date-time år 12 26)}
     {:navn "første påskedag" :dt første-påskedag}
     {:navn "skjærtorsdag" :dt (time/minus første-påskedag (time/days 3))}
     {:navn "langfredag" :dt (time/minus første-påskedag (time/days 2))}
     {:navn "Kristi himmelfartsdag" :dt (time/plus første-påskedag (time/days 39))}
     {:navn "første pinsedag" :dt (time/plus første-påskedag (time/days 49))}
     {:navn "andre pinsedag" :dt (time/plus første-påskedag (time/days 50))}]))

(defn- finn-helligdager [#^ReadableDateTime dato]
  (cond (or (nil? @ekspanderte-helligdager)
            (not= (-> @ekspanderte-helligdager first :dt time/year) (time/year dato)))
        (do (reset! ekspanderte-helligdager (helligdager (time/year dato)))
            (finn-helligdager dato))
        :else (let [result (filter #(sammenlign-dato dato (:dt %)) @ekspanderte-helligdager)]
                (if (empty? result) false result))))

(defn- sammenlign-dato [a b]
  (if (and (= (time/year a) (time/year b))
           (= (time/month a) (time/month b))
           (= (time/day a) (time/day b)))
    true false))

(defn helligdag?
  ([] (finn-helligdager (l/local-now)))
  ([#^ReadableDateTime dato] (finn-helligdager dato))
  ([#^java.lang.Number YYYY MM DD] (finn-helligdager (time/date-time YYYY MM DD))))
