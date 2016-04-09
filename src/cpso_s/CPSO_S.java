/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpso_s;
import cpso.*;

/**
 *
 * @author Peter
 */
public class CPSO_S extends CPSO {    
    
        public CPSO_S(int dimensionSize, int maxLoops, int swarmSize, double Inertia, double c1, double c2, int k)
        {
            super(dimensionSize, maxLoops, swarmSize, Inertia, c1, c2, k);
            InitializeSwarms(false);
        }
        
        //calculate the fitness of the PSO
        public void start()
        {
            for(int i = 0; i < maxLoops; i++)
            {
                for (int s = 0; s < swarms.length; s++) //iterate through swarms
                {
                    for(Particle p : swarms[s].getParticles()){ //for each particle
                        
                        double fitness = CalculateFitness(s, p.getPosition()); //calculate the new fitness
                        UpdateBests(fitness, p, swarms[s]); 
                    }
                    
                    for (Particle p : swarms[s].getParticles()) //move the particles
                    {
                        swarms[s].UpdateVelocity(p);
                        swarms[s].UpdatePosition(p);
                    } 
                    
                }
                    UpdateSolution();
            }
            
            for(int i = 0; i < solution.length; i++) //loop to print off solution
            {
                writeOutput("Solution "+(i+1)+": "+ solution[i]);
            }
            writeOutput("The final fitness value is: "+ CalculateFinalFitness());
        }
}
