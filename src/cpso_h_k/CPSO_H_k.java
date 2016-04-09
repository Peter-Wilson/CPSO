/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpso_h_k;

import cpso.*;
/**
 *
 * @author Peter
 */
public class CPSO_H_k extends CPSO {    
    
    int loops;
    boolean min = true;
    private Swarm[] swarms; 
    private Swarm pso_swarm;
    private double[] solution;
    int dimensionSize;
    int swarmSize;
    double C1 = 0.5;
    double C2 = 0.3;
    double INERTIA = 0.3;
    double PSO_C1 = 0.5;
    double PSO_C2 = 0.3;
    double PSO_INTERTIA = 0.3;
    int maxLoops;
    int k = 0;

    public CPSO_H_k(int dimensionSize, int maxLoops, int swarmSize, double Inertia, double c1, double c2, int k)
    {
        super(dimensionSize, maxLoops, swarmSize, Inertia, c1, c2, k);
        pso_swarm = new Swarm(dimensionSize, PSO_C1, PSO_C2, PSO_INTERTIA, min, 1);
        super.InitializeSwarms();
    }

    //calculate the fitness of the PSO
    @Override
    public void start()
    {
        for(int i = 0; i < maxLoops; i++)
        {

            // <editor-fold desc="CPSO swarm">
                /////////////////////////////////////////
                /////    Update the CPSO Swarms    //////
                /////////////////////////////////////////
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
            // </editor-fold>

            UpdateSolution();

            //transfer knowledge from CPSO to PSO
            if(swarms[0].getGlobalBest() != null)
            {
                double[] velocity = new double[dimensionSize];
                for(int s = 0; s < swarms.length; s++)
                {
                    for(int j = 0; j < k; j++)
                    {
                        velocity[(s*k)+j] = swarms[s].getGlobalBest().getVelocity()[j];
                    }
                }
                pso_swarm.setRandomParticle(getSolution(), velocity);
            }

            // <editor-fold desc="PSO swarm"> 
                /////////////////////////////////////////
                /////     Update the PSO Swarm     //////
                /////////////////////////////////////////                               
            for(Particle p : pso_swarm.getParticles()){ //for each particle

                double fitness = CalculateFitness(0, p.getPosition()); //calculate the new fitness
                UpdateBests(fitness, p, pso_swarm);                    
            }

            for (Particle p : pso_swarm.getParticles()) //move the particles
            {
                pso_swarm.UpdateVelocity(p);
                pso_swarm.UpdatePosition(p);
            }     
            // </editor-fold>

            //Transfer knowledge from PSO to CPSO
            if(pso_swarm.getGlobalBest() != null)
            {
                //for each swarm
                for(int s = 0; s < swarms.length; s++)
                {
                    //select a random particle to replace with the pso global best
                    double[] value = new double[k];
                    double[] velocity = new double[k];
                    for(int j = 0; j < k; j++)
                    {
                        value[j] = pso_swarm.getGlobalBest().getPosition()[(s*k)+j];
                        velocity[j] = pso_swarm.getGlobalBest().getVelocity()[(s*k)+j];
                    }
                    swarms[s].setRandomParticle(value, velocity);
                }
            }

        }

        for(int i = 0; i < solution.length; i++) //loop to print off solution
        {
            writeOutput("Solution "+(i+1)+": "+ solution[i]);
        }
        writeOutput("The final fitness value is: "+ CalculateFinalFitness());
    }

}
