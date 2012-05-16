(ns helligdager.core
  (:require [clj-time.core :as time]
            [clj-time.local :as l])
  (:import [org.joda.time ReadableDateTime]))

(def ^:private ekspanderte-helligdager (atom nil))

(defn- finn-påskedag [år]
  (let [a (mod år 19)
        b (quot år 100)
        c (mod år 100)
        d (quot b 4)
        e (mod b 4)
        f (quot (+ b 8) 25)
        g (quot (+ (- b f) 1) 3)
        h (mod (+ (- (- (+ (* 19 a) b) d) g) 15) 30)
        i (quot c 4)
        k (mod c 4)
        L (mod (- (- (+ (+ 32 (* 2 e)) (* 2 i)) h) k) 7)
        m (quot (+ (+ a (* 11 h)) (* 22 L)) 451)
        måned (quot (+ (- (+ h L) (* 7 m)) 114) 31)
        dag (+ (mod (+ (- (+ h L) (* 7 m)) 114) 31) 1)]
    (time/date-time år måned dag)))

(defn- finn-helligdager [år]
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

(defn- sammenlign-dato [a b]
  (if (and (= (time/year a) (time/year b))
           (= (time/month a) (time/month b))
           (= (time/day a) (time/day b)))
    true false))

(defn- parser [#^ReadableDateTime dato]
  (cond (or (nil? @ekspanderte-helligdager)
            (not= (-> @ekspanderte-helligdager first :dt time/year) (time/year dato)))
        (do (reset! ekspanderte-helligdager (finn-helligdager (time/year dato)))
            (parser dato))
        :else (let [result (filter #(sammenlign-dato dato (:dt %)) @ekspanderte-helligdager)]
                (if (empty? result) false result))))

(defn helligdager
  ([] (parser (l/local-now)))
  ([år] (finn-helligdager år))
  ([år måned] nil)
  ([år måned dato] (parser (time/date-time år måned dato))))
