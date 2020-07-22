package eu.delafoy.vavrintro;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import io.vavr.API;
import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

class FunctionDemo {

	@Test
	void uneFonction0VavrAmélioreSupplier() {
		Supplier<Integer> java = () -> 4;
		assertThat(java.get()).isEqualTo(4);

		Function0<Integer> vavr = (() -> 4);
		assertThat(vavr.apply()).isEqualTo(4);
		// Vavr essaie de faire implémenter a ses types leur équivalent java
		assertThat(vavr.get()).isEqualTo(4);

		// Tout en apportant quelques méthodes manquant dans Java.
		assertThat(vavr.andThen(x -> x + 2).apply()).isEqualTo(6);
	}

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
	}

	@Test
	void lesFonctionsPeuventÊtreCurryfiées() throws Exception {
		Function2<Integer, Integer, Integer> add = (x, y) -> x + y;
		Function1<Integer, Integer> addOne = add.apply(1);
		assertThat(addOne.apply(1)).isEqualTo(2);
		assertThat(addOne.apply(5)).isEqualTo(6);
	}

	@Test
	void lesFonctionsCurryfiéeSontParfaitesEnParamêtres() throws Exception {
		// Nous éviterons d’utiliser « var » et les imports statiques pour faciliter la
		// compréhension.
		// Les collections de Vavr utilisées ici sont immuable.

		Function2<Set<String>, Set<String>, Set<String>> créerUnCatalogueSaisonnier =
				(articlesPermanents, articlesSaisonniers) -> articlesPermanents.addAll(articlesSaisonniers);

		// La curryfication permet de créer une fonction dont les premiers paramètres
		// sont déjà renseignés.
		Set<String> articlesPermanents = API.Set("Pain", "Croissant", "Pain au chocolat");
		Function1<Set<String>, Set<String>> créerLeCatalogueSaisonnier =
				créerUnCatalogueSaisonnier .apply(articlesPermanents);

		// La fonction curryfiée peut ensuite être utilisée.
		Map<String, Set<String>> extras = API.Map(
				"Noël", API.Set("Buche Chocolat"),
				"Épiphanie", API.Set("Galette"));

		Map<String, Set<String>> collect = extras.mapValues(créerLeCatalogueSaisonnier);

		assertThat(collect).isEqualTo(API.Map(
				"Noël", API.Set("Pain", "Croissant", "Pain au chocolat", "Buche Chocolat"),
				"Épiphanie", API.Set("Pain", "Croissant", "Pain au chocolat", "Galette")));
	}
}
