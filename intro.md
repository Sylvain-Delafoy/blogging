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

# Les Fonctions

Java 8 et ses successeurs ont introduit les Fonctions sous la forme d'un ensemble de classes (Function, Supplier, Consummer, Bifunction,...). Les compostants Java peuvent être composés en partie (Function#compose et Function#andThen). Vavr apporte une coherence, toutes ses fonctions retournent une valeur et sont composables

```java
		Supplier<Integer> java = () -> 4;
		assertThat(java.get()).isEqualTo(4);

		Function0<Integer> vavr = (() -> 4);
		assertThat(vavr.apply()).isEqualTo(4);
		// Vavr essaie de faire implémenter a ses types leur équivalent java
		assertThat(vavr.get()).isEqualTo(4);

		// Tout en apportant quelques méthodes manquant dans Java.
		assertThat(vavr.andThen(x -> x + 2).apply()).isEqualTo(6);```

Vavr offre aussi quelques utilitaires supplémentaires.

## La mise en cache

Lorsqu'une fonction effectue un traitement coûteux, il peut être utile de stocker son résultat. En programmation fonctionnelle cela s'appelle la La [Mémoisation](https://fr.wikipedia.org/wiki/M%C3%A9mo%C3%AFsation).

```java
	@Test
	void uneFonctionEstGénéralementExecutéeAChaqueAppel() {
		AtomicInteger compteur = new AtomicInteger();
		Function1<Integer, Integer> fonctionBrute = (Integer i) -> {
			compteur.incrementAndGet();
			return i * 2;
		};

		assertThat(fonctionBrute.apply(3)).isEqualTo(6);
		assertThat(compteur).hasValue(1);
		assertThat(fonctionBrute.apply(3)).isEqualTo(6);
		assertThat(compteur).hasValue(2);
	}

	@Test
	void uneFonctionMemoïséeNeFaitPasLeTravailDeuxFois() {
		AtomicInteger compteur = new AtomicInteger();
		// Ici, il faut "aider" java pour pouvoir appeller une méthode sur la fonction.
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
	}
```
Dans le premier cas, chaque appel execute le code (incremente le compteur et multiplie le paramètre par 2) avant de retourner la valeur.

Dans le second cas, sans changement de comportement du point d'appel, le code n'est pas executé.

La [mémoisation](https://fr.wikipedia.org/wiki/M%C3%A9mo%C3%AFsation) est un outil a double tranchant, dans un programme fonctionnel idéal, tout appel de fonction avec les mêmes paramètres produit toujours les mêmes résultats. Cela est très utile pour éviter des calculs couteux, mais peut avoir des conséquences désastreuses si ce n'est pas ce qu'on attends de la fonction (par exemple: [tirer un nombre aléatoire](https://xkcd.com/221/)) ou que l'on provoque une fuite mémoire puisque la fonction garde une référence à ses paramètres et ses résultats.

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