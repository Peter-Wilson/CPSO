/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpso;

import Functions.Triangulation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.jzy3d.plot3d.builder.delaunay.jdt.Delaunay_Triangulation;
import org.jzy3d.plot3d.builder.delaunay.jdt.Point_dt;
import org.jzy3d.plot3d.builder.delaunay.jdt.Triangle_dt;


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
        Delaunay_Triangulation dt;
        int k = 0;
        int function = 0;
        double diameter = 0;

        public Swarm(int swarmSize, double C1, double C2, double INERTIA, boolean min, int k, int function)
        {
            this.min = min;
            this.swarmSize = swarmSize;
            this.C1 = C1;
            this.C2 = C2;
            this.k = k;
            this.INERTIA = INERTIA;
            this.function = function;
            this.diameter = getDiameter(function);
            InitializeParticles();
        }
        
        public boolean isDTNull()
        {
            return (this.dt == null);
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
                    position[j] = getRandomNumber(rand, function);
                
                particles[i] = new Particle(position, function);
            }
        }
        
        public static double getRandomNumber(Random rand, int function)
        {
            if(function == 0)
                return (rand.nextDouble()*getDiameter(function))+1;
            
            return (rand.nextDouble()*(getDiameter(function)*2))-(getDiameter(function));
        }
        
        public static final double getDiameter(int function)
        {
            switch(function)
            {
                case 0: return 50;
                case 1: return 100;
                case 2: return 5.12;
                case 3: return 30;
                case 4: return 600; 
                case 5: return 32;
                default: return -1;
            }
        }
        
        //Update the velocity of the points based on the velocity solution
        public void UpdateVelocity(Particle p)
        {
            UpdateVelocity(p, 0, false);
        }
        
        //Update the velocity of the points based on the velocity solution
        public void UpdateVelocity(Particle p, double loopPercent)
        {
            UpdateVelocity(p, loopPercent, false);
        }
        

        //Update the velocity of the points based on the velocity solution
        public void UpdateVelocity(Particle p, double loopPercent, boolean test)
        {
            Random random = new Random();
            double R1 = random.nextDouble();
            double R2 = random.nextDouble();
            double inertia = INERTIA - (INERTIA*loopPercent); //inertia should decrease consistently as the cpso continues
            double velocity = 0;
            
            //for testing purposes remove the randomness
            if(test){ R1 = 1; R2 = 1; }

            for(int i = 0; i < k; i ++)
            {
            
                if(dt == null || p.getSocialNeighbour() == null)
                {
                    velocity = ((inertia * p.getVelocity()[i]) +
                                 C1 * R1 * (p.getpBest()[i] - p.getPosition()[i]) +
                                 C2 * R2 * (getGlobalBest().getpBest()[i] - p.getPosition()[i]));
                }
                else
                {
                    velocity = ((inertia * p.getVelocity()[i]) +
                                 C1 * R1 * (p.getpBest()[i] - p.getPosition()[i]) +
                                 C2 * R2 * (p.getSocialNeighbour().getpBest()[i] - p.getPosition()[i]));
                }
                
                p.setVelocity(velocity, i, function);
            }
        }

        //Use the velocity to update the position
        public void UpdatePosition(Particle p)
        {
            for(int i = 0; i < k; i++)
            {
                p.setPosition(p.getPosition()[i] + p.getVelocity()[i], i, function);
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
        
        /**
         * Sets the value of a random particle to the supplied value
         * @param position the position to set at the random index
         * @param velocity the velocity to set at the ransom index
         * @return true or false if the change was completed
         */
        public boolean setRandomParticle(double[] position, double[] velocity)
        {
            if(position.length != k)
                return false;
            else
            {
                int randomIndex = 0;
                do{
                    randomIndex = (int)(Math.random()*swarmSize);
                }
                while(particles[randomIndex] == this.globalBest);
                
                particles[randomIndex].setPosition(position, function);
                particles[randomIndex].setVelocity(velocity, function);
                return true;
            }
        }
        
        /**
         * Creates the Delaunay Triangulation
         */
        public void CalculateDelaunayTriangulation()
        {
            Point_dt[] points = new Point_dt[particles.length];
            dt = new Delaunay_Triangulation();
            //System.out.println("Creating DT....");
            try{
                for(int i = 0; i < particles.length; i++)
                {
                    Point_dt a = Triangulation.convertParticletoPoint(particles[i]);
                    //System.out.print("Adding point: "+i+"...["+a.x()+","+a.y()+","+a.z()+"]");
                    dt.insertPoint(a); 
                    //System.out.println("...Point added");
                    dt.insertPoint(Triangulation.convertParticletoPoint(particles[i])); 
                }
            }
            catch(Exception e)
            {
                //if the delaunay triangulation cannot be created, default it to
                //null, which will be handled in the other function
                dt = null;
            }
            //System.out.println("...DT Created");
        }
        
          // Implementing Fisher–Yates shuffle
          static void shuffleArray(Point_dt[] ar)
          {
            Random rnd = new Random();
            for (int i = ar.length - 1; i > 0; i--)
            {
              int index = rnd.nextInt(i + 1);
              // Simple swap
              Point_dt a = ar[index];
              ar[index] = ar[i];
              ar[i] = a;
            }
          }
        
        
        /**
         * Returns the best item to swap with based on the criteria noted
         * @param item the item that is looking to swap
         * @return the item that is best to swap with
         */
        public Particle chooseBestNeighbour(Particle item, CPSO cpso, int index)
        {
            if(dt == null) return null;
            
            boolean hasConnectedNeighbours = false;
            Point_dt particlePoint = Triangulation.convertParticletoPoint(item);
            
            ArrayList<Point_dt> connected = new ArrayList<Point_dt>();
            
            if(particles[0].getPosition().length == 1)
            {
                addClosestParticles1D(item, connected);
            }
            else
            {
                Iterator<Triangle_dt> iterator;
                
                //Get the triangles
                try
                {
                    iterator = dt.trianglesIterator();
                }catch(NullPointerException e)
                {
                    //System.out.println("Delaunay Triangulation cannot be created");
                    return null;
                }
                
                while(iterator.hasNext())
                {
                    Triangle_dt triangles = iterator.next();                
                    if(triangles.contains(particlePoint))
                    {
                        connected.add(triangles.p1());
                        connected.add(triangles.p2());
                        connected.add(triangles.p3());
                    }
                }
            }
            
            //Pf = min(Nk)
            Point_dt point = Triangulation.closestNeighbour(particlePoint, connected);
            Point_dt temp = null;
            
            //for k = 1 to neighbourset_size do
            for(int i = 0; i < connected.size(); i++)
            {
                if(connected.get(i) == particlePoint) continue;
                
                //if working_together(Pi, Pk)and
                if(Triangulation.working_together(particlePoint, connected.get(i), particles))
                {
                    //if dist(Xi − Pc) < dist(Xi − Pk)and fitness(Pk) < fitness(Xi) then
                    if(particlePoint.distance3D(point) < particlePoint.distance3D(connected.get(i)) &&
                        cpso.CalculateFitness(index, Triangulation.getParticle(connected.get(i), particles).getPosition()) < 
                        cpso.CalculateFitness(index, Triangulation.getParticle(particlePoint, particles).getPosition()))
                    {
                        temp = connected.get(i);
                        hasConnectedNeighbours = true;
                    }
                }
            }
            
            //localExploitationRatio = swarm.diameter/200
            double localExploitationRatio = diameter/200;
            
            if(hasConnectedNeighbours &&
                    particlePoint.distance3D(temp)/ particlePoint.distance3D(point) >
                    localExploitationRatio &&
                    Triangulation.lengthVector(item.getVelocity()) < 2*particlePoint.distance3D(temp))
            {
                return Triangulation.getParticle(temp, particles);
            }
            else
            {
                return Triangulation.getParticle(point, particles);
            }  
            
        }

    public void addClosestParticles1D(Particle item, ArrayList<Point_dt> connected) {
        double closestAbove = Integer.MIN_VALUE;
        Particle above = item;
        double closestBelow = Integer.MAX_VALUE;
        Particle below = item;
        if(item.getPosition().length > 1) return;
        
        for(Particle p : particles)
        {
            if(p == item) continue;
            double difference =  item.getPosition()[0] - p.getPosition()[0];
            if(difference < 0 && difference > closestAbove){
                closestAbove = difference;
                above = p;
            }
            else if(difference > 0 && difference < closestBelow){
                closestBelow = difference;
                below = p;
            }
        }
        
        connected.add(Triangulation.convertParticletoPoint(above));
        connected.add(Triangulation.convertParticletoPoint(below));
    }

    

    }
