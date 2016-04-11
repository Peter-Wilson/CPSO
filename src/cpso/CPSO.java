/*
 * The skeleton CPSO program
 * This is extended by all the variants
 */
package cpso;

/**
 *
 * @author Peter
 */
public class CPSO {
    
    public int loops;
    public boolean min = true;
    public Swarm[] swarms; 
    public double[] solution;
    public int dimensionSize;
    public int swarmSize;
    public double C1 = 0.5;
    public double C2 = 0.3;
    public double INERTIA = 0.3;
    public int maxLoops;
    public int numSwarms;
    
    public CPSO(int dimensionSize, int maxLoops, int swarmSize, double Inertia, double c1, double c2, int numSwarms)
    {
        this.dimensionSize = dimensionSize;
        this.maxLoops = maxLoops;
        this.swarmSize = swarmSize;
        this.INERTIA = Inertia;
        this.C1 = c1;
        this.C2 = c2;
        if(numSwarms > dimensionSize) numSwarms = dimensionSize;
        this.numSwarms = numSwarms;            
        solution = new double[dimensionSize];
    }

    /**
     * Creates the swarms
     */
    public void InitializeSwarms(boolean random)
    {
        int remaining = 0;
        int count = 0;
        swarms = new Swarm[numSwarms];
        for(int i = 0; i < numSwarms; i++)
        {
            int width = 0;
            
            if(!random)
            {
                if(i%numSwarms == 0)
                    width = (int)(Math.ceil(((double)dimensionSize/numSwarms)));
                else
                    width = (int)(Math.floor(((double)dimensionSize/numSwarms)));
            }
            else
            {
                if(i == numSwarms-1)
                    width = (int)(Math.ceil(((double)dimensionSize/numSwarms)))+remaining;
                else
                {
                    //select the random grouping into k subgroups
                    int randomValue = (int)((Math.random())*(((double)dimensionSize/numSwarms)))+1;                
                    width = remaining + randomValue;
                    remaining = (int)(Math.ceil(((double)dimensionSize/numSwarms)-randomValue));
                }
            }
            
            swarms[i] = new Swarm(swarmSize, C1, C2, INERTIA, min, width);

            for(int j = 0; j < width; j++)
            {
                solution[count++] = swarms[i].getParticles()[0].getPosition()[j];
            }
        }
    }

    /**
     * Takes the new fitness and particle and determine if it is the new 
     * global best and or personal best
     * @param fitness the new fitness value
     * @param p the particle that is being updated
     * @param swarm the swarm that particle is from
     */
    public void UpdateBests(double fitness, Particle p, Swarm swarm)
    {
        if (p.UpdatePersonalBest(fitness, p.getPosition(), min))  //update the personal best
            writeOutput("New Personal best for " + p + ": x=" + p.getPosition());

        if ((swarm.getGlobalBest() == null) ||
            (p.getFitness() < swarm.getGlobalBest().getFitness() && min) ||
            (swarm.getGlobalBest().getFitness() < p.getFitness() && !min))      //update the global best
        {
            swarm.setGlobalBest(p);
            writeOutput("New Global Best for Swarm " + swarm + ": x=" + p.getPosition());
        }
    }

     /**
     * Update to the best current solution by taking the global best values
     */
    public void UpdateSolution()
    {
        int index = 0;
        for(int i = 0; i < swarms.length; i++)
        {
            Particle best = swarms[i].getGlobalBest();
            if(best == null){
                index+= swarms[i].getParticles()[0].getPosition().length;
                continue;
            }
            else{
                for(int j = 0; j < best.getPosition().length; j++)
                {
                    solution[index++] = best.getPosition()[j];
                }
            }
        }
    }

    /**
     * Calculate the fitness of the current swarm
     * @param position the current position
     * @return 
     */
    public double CalculateFitness(int index, double[] position, int numSwarms)
    {
        double fitness = 0;
        int count = 0;
        if(numSwarms == 1)
        {
            for(int i = 0; i < position.length; i++)
            {
                fitness += Math.log(position[i]);
            }
        }
        else
        {
            for(int i = 0; i < numSwarms; i++)
            {
                if(i == index)
                    for(int j = 0; j < swarms[i].getParticles()[0].getPosition().length; j++)
                    {
                        fitness += Math.log(position[j]);
                        count++;
                    }
                else
                    for(int j = 0; j < swarms[i].getParticles()[0].getPosition().length; j++)
                        fitness += Math.log(solution[count++]);
            }
        }
        return fitness;   
    }

    /**
     * Returns the final fitness based on the solution function
     * @return fitness
     */
    public double CalculateFinalFitness()
    {
        double fitness = 0;
        for(int i = 0; i < dimensionSize; i++)
        {
            fitness += Math.log(solution[i]);
        }
        return fitness; 
    }

    public void writeOutput(String output)
    {
        System.out.println(output);
    }
       
    /**
     * @return the swarms
     */
    public Swarm[] getSwarms() {
        return swarms;
    }

    /**
     * @return the solution
     */
    public double[] getSolution() {
        return solution;
    }

    /**
     * @param solution the solution to set
     * @throws exception incorrect solution size
     */
    public void setSolution(double[] solution) throws Exception {
        if(solution.length == dimensionSize)
        {
            this.solution = solution;
        }
        else throw new Exception("Incorrect solution size");
    }
}
