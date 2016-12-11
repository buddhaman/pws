package system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Settings;

import component.Bot;
import creature.CreatureBody;
import entity.Factory;
import genome.Genome;
import physics.Tile;
import simulation.Simulation;

public class EvolutionSystem extends EntitySystem {

	private Family family = Family.all(Bot.class).get();

	private ComponentMapper<Bot> botM = Mappers.botMapper;

	private Simulation simulation;
	private ImmutableArray<Entity> bots;

	public float bestProb = .1f;

	public int ticksClearList = 25000;
	public int ticks;

	public float mateSearchRadius = 100;

	public EvolutionSystem(Simulation simulation) {
		bots = simulation.engine.getEntitiesFor(family);
		this.simulation = simulation;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		if (++ticks > ticksClearList) {
			ticks -= ticksClearList;
			simulation.genePool.clearList();
		}

		int botNum = bots.size();

		if (botNum < simulation.minBots) {
			Vector2 pos = simulation.getFreePosition();
			Tile t = simulation.world.getTileAt(pos.x, pos.y);
			if (t.energy >= CreatureBody.REPRODUCTION_COST) {
				t.energy -= CreatureBody.REPRODUCTION_COST;
				Entity newBot = Factory
						.createBot(MathUtils.random() < bestProb ? simulation.genePool.getRandomBestGenome(simulation)
								: simulation.genePool.getRandomGenome(simulation), pos.x, pos.y);
				simulation.addEntity(newBot);
			}

		}
		for (int i = 0; i < botNum; i++) {
			Entity bot = bots.get(i);
			botM.get(bot).body.isTarget = false;
		}
		for (int i = 0; i < botNum; i++) {
			Entity bot = bots.get(i);
			CreatureBody creature = botM.get(bot).body;
			updateReproduction(creature, botNum);
		}
	}

	public CreatureBody selectFittestCreature(CreatureBody c) {
		float max = 0;
		CreatureBody fittest = null;
		for (int i = 0; i < bots.size(); i++) {
			Entity bot = bots.get(i);
			Bot bc = botM.get(bot);
			CreatureBody cb = bc.body;
			if (cb == c)
				continue;
			if (Vector2.dst2(c.group.getX(), c.group.getY(), cb.group.getX(), cb.group.getY()) > mateSearchRadius
					* mateSearchRadius)
				continue;

			float totalScore = cb.getFitness();
			if (totalScore > max || max == 0) {
				fittest = cb;
				max = totalScore;
			}
		}
		return fittest;
	}

	private void updateReproduction(CreatureBody creature, int botNum) {
		if (creature.mating) {
			creature.matingTarget.isTarget = true;
			if (creature.matingTarget.isDead()) {
				CreatureBody target = selectFittestCreature(creature);
				if (target == null) {
					spawnCopy(creature, botNum);
					creature.mating = false;
					creature.matingTarget = null;
				} else {
					creature.matingTarget = target;
				}

			}
		}
		if (creature.canReproduce() && !creature.mating) {
			if (MathUtils.random() < Settings.getCurrent().matingProb.val) {
				CreatureBody fittest = selectFittestCreature(creature);
				if (fittest != null) {
					creature.matingTarget = fittest;
					creature.mating = true;
				} else
					spawnCopy(creature, botNum);
			} else {
				for (int i = 0; i < 1; i++) {
					spawnCopy(creature, botNum);
				}
			}
		}

	}

	public void spawn(Genome gene, float x, float y) {
		Entity bot = Factory.createBot(gene, x, y);
		simulation.addEntity(bot);
	}

	private void spawnCopy(CreatureBody creature, int botNum) {
		if (botNum > simulation.maxBots)
			return;
		Genome nGenome = new Genome(creature.genome);
		creature.energy -= CreatureBody.REPRODUCTION_COST;
		nGenome.mutate();
		Entity bot = Factory.createBot(nGenome, creature.group.getX() + MathUtils.random(),
				creature.group.getY() + MathUtils.random());
		simulation.addEntity(bot);
	}

}
