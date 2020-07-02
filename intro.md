# Mettez un pied dans le futur de java et le présent de Scala avec Vavr

## Introduction
L’écosystème java évolue vers les principes fonctionnels de nombreux pas restent encore a franchir. Nombres de changements (plus ou moins) récents participent à l’arrivée du paradigme fonctionnel dans l'écosystème java :
* Java 8 et les lambdas des Functions et des Streams en java 8,
* Les reactive streams dans Java ([java.util.concurrent.Flow](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/Flow.html)) et le framework omniprésent Spring de reactive streams, ([Reactor](https://projectreactor.io/))
* Des changements plus profonds les switch expressions (Java 14), ou les Records (en preview) etc…

D’autres langages comme Scala incluent des paradigmes, structures et syntaxes plus adaptées à la programmation fonctionnelle. Java ne pourra pas arriver au même confort d'utilisation de ces paradigmes sans modifications profondes du langage mais l'usage de librairies peut permettre de prendre un peu d'avance sur le langage.

Ce que Jodatime à fait pour la manipulation du temps, Vavr se propose de le faire pour la composition de fonction, l’expressivité des types et les performances des collections dans le domaine fonctionnel.
La librairie suit de près les changements dans Java et n'hésite pas à s'adapter, lorsqu’elle le juge pertinent en supprimant certains composants.

L’utilisation d’une telle librairie ne doit pas se faire dans le but d'apprendre à faire du scala sans changer de langage. Il faut aussi toujours pondérer les pré-requis et les avantages de la librairie et de ses concurrents pour ne pas devoir entrer en guerre avec son Framework ou son langage. Le blog du projet est d’ailleurs transparent sur les objectifs du projet: Une API simple, être aussi rigoureux que possible en termes de types et ne pas refaire ce que fait déjà Java.
![
    @startmindmap headdump
    * vavr
    **[#grey] Dans le sens de l'évolution de java
    ***[#grey]  Java migre vers des éléments Immuables (Records) mais le framework de collections ne suis pas.
    ***[#grey]  Les reactive streams normalisés dans la JVM
    ***[#grey]  L'écosystème suit: Spring avec Reactor par exemple
    ** Collections
    *** Les Collections sont iterables
    *** Combinaisons
    *** Un clone de trucs en scala
    ** Pattern matching
    ** for comprehension
    *** Les switch expressions
    ** utilité
    ** Des optimisations et des ajouts
    *** Tuple
    *** Either
    *** Validation
    *** Try
    *** Streams
    *** Options
    *** les switch expressions
    ** Les classes peuvent être converties en java
    *** Functions
    **** CheckedFunctions
    @endmindmap
](documentation/assets/headdump.png)