# helligdager

Et bibliotek for å finne norske helligdager og flaggdager. Dagen for Stortingsvalg er en offisiell flaggdag, men denne er (foreløpig) ubestemmelig av biblioteket. Valgdagen bestemmes av Stortinget hvert valgår, og er alltid en mandag i september. Søndager er også offisielle helligdager, men dette returneres ikke fra `(helligdag)`-funksjonen.

## Usage

Finn hellidager på en spesifikk dato:

     => (helligdager 2012 5 17)

     ({:navn "Grunnlovsdagen", :dt #<DateTime 2012-05-17T00:00:00.000Z>}
      {:navn "Kristi himmelfartsdag", :dt #<DateTime 2012-05-17T00:00:00.000Z>})

Finn flaggdager i en måned:

    => (flaggdager 2012 7)

    ({:navn "H.M. Dronning Sonjas fødselsdag", :dt #<DateTime 2012-07-04T00:00:00.000Z>}
     {:navn "H.K.H. Kronprins Haakon Magnus' fødselsdag", :dt #<DateTime 2012-07-20T00:00:00.000Z>}
     {:navn "Olsokdagen", :dt #<DateTime 2012-07-29T00:00:00.000Z>})

`(helligdager 2012)` returnerer alle helligdager for år 2012, `(helligdager)` returnerer dagens helligdager.


## License

Copyright (C) 2012 Aleksander Skjæveland Larsen

Distributed under the Eclipse Public License, the same as Clojure.
