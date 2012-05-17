(ns helligdager.core
  (:require [clj-time.core :as time]
            [clj-time.local :as l])
  (:import [org.joda.time ReadableDateTime]))

(def ^:private ekspanderte-dager (atom {}))

(defn- finn-påskedag
  "Anonymous Gregorian algorithm"
  [år]
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

(defn- finn-helligdager
  "http://www.lovdata.no/all/tl-19950224-012-0.html#2"
  [år]
  (let [første-påskedag (finn-påskedag år)]
    [{:navn "Nyttårsdag" :dt (time/date-time år 1 1)}
     {:navn "Arbeidernes internasjonale kampdag" :dt (time/date-time år 5 1)}
     {:navn "Grunnlovsdagen" :dt (time/date-time år 5 17)}
     {:navn "første juledag" :dt (time/date-time år 12 25)}
     {:navn "andre juledag" :dt (time/date-time år 12 26)}
     {:navn "første påskedag" :dt første-påskedag}
     {:navn "skjærtorsdag" :dt (time/minus første-påskedag (time/days 3))}
     {:navn "langfredag" :dt (time/minus første-påskedag (time/days 2))}
     {:navn "Kristi himmelfartsdag" :dt (time/plus første-påskedag (time/days 39))}
     {:navn "første pinsedag" :dt (time/plus første-påskedag (time/days 49))}
     {:navn "andre pinsedag" :dt (time/plus første-påskedag (time/days 50))}]))


(defn- finn-flaggdager
  "http://www.lovdata.no/for/sf/ud/xd-19271021-9733.html#4"
  [år]
  (let [første-påskedag (finn-påskedag år)]
    [{:navn "Nyttårsdag" :dt (time/date-time år 1 1)}
     {:navn "H.K.H. Prinsesse Ingrid Alexandras fødselsdag" :dt (time/date-time år 1 21)}
     {:navn "Samefolkets dag" :dt (time/date-time år 2 6)}
     {:navn "H.M. Kong Harald Vs fødselsdag" :dt (time/date-time år 2 21)}
     {:navn "Arbeidernes internasjonale kampdag" :dt (time/date-time år 5 1)}
     {:navn "Frigjøringsdagen 1945" :dt (time/date-time år 5 8)}
     {:navn "Grunnlovsdagen" :dt (time/date-time år 5 17)}
     {:navn "Unionsoppløsningen 1905" :dt (time/date-time år 6 7 )}
     {:navn "H.M. Dronning Sonjas fødselsdag" :dt (time/date-time år 7 4)}
     {:navn "H.K.H. Kronprins Haakon Magnus' fødselsdag" :dt (time/date-time år 7 20)}
     {:navn "Olsokdagen" :dt (time/date-time år 7 29)}
     {:navn "H.K.H. Kronprinsesse Mette-Marits fødselsdag" :dt (time/date-time år 8 19)}
     {:navn "første juledag" :dt (time/date-time år 12 25)}
     {:navn "første påskedag" :dt første-påskedag}
     {:navn "første pinsedag" :dt (time/plus første-påskedag (time/days 49))}
     ;{:navn "Dagen for Stortingsvalg" :dt "En mandag i september" ;(time/date-time år)}
     ]))

(defn- sammenlign-dato [a b]
  (if (and (= (time/year a) (time/year b))
           (= (time/month a) (time/month b))
           (= (time/day a) (time/day b)))
    true false))

(defn- dato->key [#^ReadableDateTime dato]
  (-> dato time/year str keyword))

(defn- finn-type [type år]
  (let [key (-> år str keyword)
        dager (get (get @ekspanderte-dager key) type)
        finn-fn (get {:helligdager finn-helligdager
                      :flaggdager finn-flaggdager} type)]
    (if (empty? dager)
      (do (->> (assoc (get @ekspanderte-dager key) type (finn-fn år))
               (assoc @ekspanderte-dager key)
               (reset! ekspanderte-dager))
          (finn-type type år))
      dager)))

(defn- parser
  ([type] (finn-type type (time/year (l/local-now))))
  ([type år] (finn-type type år))
  ([type år måned]
     (->> (finn-type type år)
          (filter #(= (time/month (:dt %)) måned))))
  ([type år måned dag]
     (let [dato (time/date-time år måned dag)]
       (->> (finn-type år type)
            (filter #(sammenlign-dato (:dt %) dato))))))

(defn- med-helligdag []
  (partial parser :helligdager))

(defn- med-flaggdag []
  (partial parser :flaggdager))

(defn flaggdager
  ([] ((med-flaggdag)))
  ([år] ((med-flaggdag) år))
  ([år måned] ((med-flaggdag) år måned))
  ([år måned dato] ((med-flaggdag) år måned dato)))

(defn helligdager
  ([] ((med-helligdag)))
  ([år] ((med-helligdag) år))
  ([år måned] ((med-helligdag) år måned))
  ([år måned dato] ((med-helligdag) år måned dato)))
