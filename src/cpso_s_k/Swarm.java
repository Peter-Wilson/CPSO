/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpso_s_k;

import java.util.Random;

/**
 *
 * @author Peter
 */
public class Swarm
    {
        private Particle[] particles;
        private Particle globalBest;
        boolean min = true;
        int swarmSize;
        double C1 = 1;
        double C2 = 1;
        double INERTIA = 1;
        int k = 0;

        public Swarm(int swarmSize, double C1, double C2, double INERTIA, boolean min, int k)
        {
            this.min = min;
            this.swarmSize = swarmSize;
            this.C1 = C1;
            this.C2 = C2;
            this.k = k;
            this.INERTIA = INERTIA;
            InitializeParticles();
        }

        private void InitializeParticles()
        {
            setGlobalBest(null);
            particles = new Particle[swarmSize];
            
            Random rand = new Random();
            for (int i = 0; i < swarmSize; i++)
            {
                double[] position = new double[k];
                for(int j = 0; j < k; j++)
                    position[j] = (rand.nextDouble()* 50)+1;
                
                particles[i] = new Particle(position);
            }
        }
        
        //Update the velocity of the points based on the velocity solution
        public void UpdateVelocity(Particle p)
        {
            UpdateVelocity(p, false);
        }
        

        //Update the velocity of the points based on the velocity solution
        public void UpdateVelocity(Particle p, boolean test)
        {
            Random random = new Random();
            double R1 = random.nextDouble();
            double R2 = random.nextDouble();
            
            //for testing purposes remove the randomness
            if(test){ R1 = 1; R2 = 1; }

            for(int i = 0; i < k; i ++)
            p.setVelocity((INERTIA * p.getVelocity()[i]) +
                         C1 * R1 * (p.getpBest()[i] - p.getPosition()[i]) +
                         C2 * R2 * (getGlobalBest().getPosition()[i] - p.getPosition()[i]), i);
        }

        //Use the velocity to update the position
        public void UpdatePosition(Particle p)
        {
            for(int i = 0; i < k; i++)
            {
                p.setPosition(p.getPosition()[i] + p.getVelocity()[i], i);
            }
        }

        /**
         * @return the particles
         */
        public Particle[] getParticles() {
            return particles;
        }

        /**
         * @return the globalBest
         */
        public Particle getGlobalBest() {
            return globalBest;
        }

        /**
         * @param globalBest the globalBest to set
         */
        public void setGlobalBest(Particle globalBest) { 
            this.globalBest = globalBest;
        }

    }
