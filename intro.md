# Mettez un pied dans le futur de java et le présent de Scala avec Vavr

## Introduction
L’écosystème java évolue vers les principes fonctionnels de nombreux pas restent encore à franchir. Nombres de changements (plus ou moins) récents participent à l’arrivée du paradigme fonctionnel dans l’écosystème java :
* Java 8 et les lambdas des Functions et des Streams en java 8,
* Les reactive streams dans Java ([java.util.concurrent.Flow](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/Flow.html)) et le framework omniprésent Spring de reactive streams, ([Reactor](https://projectreactor.io/))
* Des changements plus profonds comme les switch expressions (Java 14) ou les Records (en preview).

D’autres langages comme Scala incluent des paradigmes, structures et syntaxes plus adaptées à la programmation fonctionnelle. Java ne pourra pas arriver au même confort d’utilisation de ces paradigmes sans modifications profondes du langage mais l’usage de librairies peut permettre de prendre un peu d’avance sur le langage.

Ce que Jodatime à fait pour la manipulation du temps, Vavr se propose de le faire pour la composition de fonction, l’expressivité des types et les performances des collections dans le domaine fonctionnel.
La librairie suit de près les changements dans Java et n’hésite pas à s’adapter, lorsqu’elle le juge pertinent en supprimant certains composants.

L’utilisation d’une telle librairie ne doit pas se faire dans le but d’apprendre à faire du Scala sans changer de langage. Il faut aussi toujours pondérer les pré-requis et les avantages de la librairie et de ses concurrents pour ne pas devoir entrer en guerre avec son Framework ou son langage. Le blog du projet est d’ailleurs transparent sur les objectifs du projet: Une API simple, être aussi rigoureux que possible en termes de types et ne pas refaire ce que fait déjà Java.

# Les Fonctions

Java 8 et ses successeurs ont introduit les Fonctions sous la forme d’un ensemble de classes (Function, Supplier, Consummer, Bifunction…). Les composants Java peuvent être composés en partie (Function#compose et Function#andThen). Vavr apporte une cohérence, toutes ses fonctions retournent une valeur et sont composables

```java
Supplier<Integer> java = () -> 4;
assertThat(java.get()).isEqualTo(4);

Function0<Integer> vavr = (() -> 4);
assertThat(vavr.apply()).isEqualTo(4);
// Vavr essaie de faire implémenter a ses types leur équivalent java
assertThat(vavr.get()).isEqualTo(4);

// Tout en apportant quelques méthodes manquant dans Java.
assertThat(vavr.andThen(x -> x + 2).apply()).isEqualTo(6);
```

Vavr offre aussi quelques utilitaires supplémentaires.

## La mise en cache

Lorsqu’une fonction effectue un traitement coûteux, il peut être utile de stocker son résultat. En programmation fonctionnelle cela s’appelle la  [Mémoïsation](https://fr.wikipedia.org/wiki/M%C3%A9mo%C3%AFsation).

Dans le cas normal, une fonction est exécutée à chaque appel.

```java
AtomicInteger compteur = new AtomicInteger();
Function1<Integer, Integer> fonctionBrute = (Integer i) -> {
    compteur.incrementAndGet();
    return i * 2;
};

assertThat(fonctionBrute.apply(3)).isEqualTo(6);
assertThat(compteur).hasValue(1);
assertThat(fonctionBrute.apply(3)).isEqualTo(6);
assertThat(compteur).hasValue(2);
```

Avec la mémoïsation, on embarque dans l’objet fonction un cache qui est utilisé plutôt que d’exécuter les instructions à chaque appel.

```java
AtomicInteger compteur = new AtomicInteger();
// Ici, il faut « aider » java pour pouvoir appeler une méthode sur la fonction.
Function1<Integer, Integer> fonctionMemoïzée = API.Function((Integer i) -> {
    compteur.incrementAndGet();
    return i * 2;
}).memoized();
assertThat(fonctionMemoïzée.apply(3)).isEqualTo(6);
assertThat(compteur).hasValue(1);
assertThat(fonctionMemoïzée.apply(4)).isEqualTo(8);
assertThat(compteur).hasValue(2);
assertThat(fonctionMemoïzée.apply(3)).isEqualTo(6);
assertThat(compteur).hasValue(2);
```
Dans le premier cas, chaque appel exécute le code (incrémente le compteur et multiplie le paramètre par 2) avant de retourner la valeur.

Dans le second cas, sans changement de comportement du point d'appel, le code n'est pas exécute.

La [mémoïsation](https://fr.wikipedia.org/wiki/M%C3%A9mo%C3%AFsation) est un outil a double tranchant, dans un programme fonctionnel idéal, tout appel de fonction avec les mêmes paramètres produit toujours les mêmes résultats. Cela est très utile pour éviter des calculs coûteux, mais peut avoir des conséquences désastreuses si ce n'est pas ce qu'on attends de la fonction (par exemple: [tirer un nombre aléatoire](https://xkcd.com/221/)) ou que l'on provoque une fuite mémoire puisque la fonction garde une référence à ses paramètres et ses résultats.

## La curryfication

Dans le paradigme fonctionnel, il est possible de construire un appel d’une fonction petit à petit et lorsque tous les paramètres sont renseignés, l’appel réel est effectué. Comme le concept est peut être assez difficile à appréhender voyons un exemple:
```java
Function2<Integer, Integer, Integer> add = (x, y) -> x + y;
Function1<Integer, Integer> addOne = add.apply(1);
assertThat(addOne.apply(1)).isEqualTo(2);
assertThat(addOne.apply(5)).isEqualTo(6);
```

L’intérêt n’est pas flagrant ainsi, mais les fonctions curryfiées peuvent être utilisées ensuite comme paramètre d’une « [fonction d’ordre supérieur](https://fr.wikipedia.org/wiki/Fonction_d%27ordre_sup%C3%A9rieur) ». C’est-à-dire une fonction qui en manipule d’autres en paramètres ou en retour.

```java
// Nous éviterons d’utiliser « var » et les imports statiques pour faciliter la compréhension.
// Les collections de Vavr utilisées ici sont immuable.

Function2<Set<String>, Set<String>, Set<String>> créerUnCatalogueSaisonnier =
        (articlesPermanents, articlesSaisonniers) -> articlesPermanents.addAll(articlesSaisonniers);

// La curryfication permet de créer une fonction dont les premiers paramètres sont déjà renseignés.
Set<String> articlesPermanents = API.Set("Pain", "Croissant", "Pain au chocolat");
Function1<Set<String>, Set<String>> créerLeCatalogueSaisonnier = créerUnCatalogueSaisonnier
        .apply(articlesPermanents);

// La fonction curryfiée peut ensuite être utilisée.
Map<String, Set<String>> extras = API.Map(
        "Noël", API.Set("Buche Chocolat"),
        "Épiphanie", API.Set("Galette"));

Map<String, Set<String>> collect = extras.mapValues(créerLeCatalogueSaisonnier);

assertThat(collect).isEqualTo(API.Map(
        "Noël", API.Set("Pain", "Croissant", "Pain au chocolat", "Buche Chocolat"),
        "Épiphanie", API.Set("Pain", "Croissant", "Pain au chocolat", "Galette")));
```

Ici, on utilise la fonction currifiée pour transformer les valeurs d’une `io.vavr.Map`, mais puisque les types Vavr sont des spécialisations de types Java aussi souvent que possible, elles peuvent être utilisées dans les Streams et Collections java, ou dans des librairies comme [Reactor](https://projectreactor.io/).

## Et les Exceptions

L'un des freins à l'adoption des librairies et nouveautés du langage d'inspiration fonctionnelles sont les exception "checked". Certaines doivent absolument être traités, mais le contexte jour énormément. Par exemple, une erreur de conversion d'une chaine de caractères en entier:
* Dans le cadre d'une propriété de configuration n'a pas forcément besoin d'être interceptée l'application ne peut fonctionner sans.
* Dans le cadre d'une saisie utilisateur dans un formulaire, mieux vaut fournir un message d'erreur que de faire échouer le traitement.

Dans Java, ce type de distinctions se fait au niveau du langage ce qui est souvent inadapté. Et cela se ressent plus depuis java 8.
```java

```
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
    ** Functions
    *** CheckedFunctions
    @endmindmap
](documentation/assets/headdump.png)