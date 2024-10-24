import java.io.*;

public class Steps {
    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.out.println("Usage: java Steps <number of steps> <length of each step> <angle between steps>");
            System.exit(1);
        }
        int n = 0;
        double a = 0, alpha = 0;
        try {
            n = Integer.valueOf(args[0]).intValue();
            a = Double.valueOf(args[1]).doubleValue();
            alpha = Double.valueOf(args[2]).doubleValue();
            if (n <= 0 || a < 0.5)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("n must be an integer > 0");
            System.out.println("a must be a real number >= 0.5");
            System.out.println("alpha must be a real number");
            System.exit(1);
        }
        new StepsObj(n, a, alpha, args[3]);
    }
}

class StepsObj {
    FileWriter fw;

    StepsObj(int n, double a, double alphaDeg, String fileName) throws IOException {
        fw = new FileWriter(fileName);

        Point3D[] vertices = new Point3D[8 * n];
        Point3D axisStart = new Point3D(0, 0, 0);
        Point3D axisEnd = new Point3D(0, 0, 1);

        for (int step = 0; step < n; step++) {
            double rotationAngle = Math.toRadians(alphaDeg * step);
            double zTop = step + 1; 
            double adjustedA = a - step; 
            double adjustedB = adjustedA - 1; 
            

            // the bottom face vertices
            vertices[step * 8] = new Point3D(adjustedA, -a, 0);
            vertices[step * 8 + 1] = new Point3D(adjustedA, a, 0);
            vertices[step * 8 + 2] = new Point3D(adjustedB, a, 0);
            vertices[step * 8 + 3] = new Point3D(adjustedB, -a, 0);
             // the top face's vertices
            vertices[step * 8 + 4] = new Point3D(adjustedA, -a, zTop);
            vertices[step * 8 + 5] = new Point3D(adjustedA, a, zTop);
            vertices[step * 8 + 6] = new Point3D(adjustedB, a, zTop);
            vertices[step * 8 + 7] = new Point3D(adjustedB, -a, zTop);

            // Initialize rotation for this step using Rota3D
            Rota3D.initRotate(axisStart, axisEnd, rotationAngle * step);

            // Apply rotation using Rota3D
            for (int i = 0; i < 8; i++) {
                int index = step * 8 + i;
                vertices[index] = Rota3D.rotate(vertices[index]);
            }

        }

        // Write vertices to the file
        for (int i = 0; i < vertices.length; i++) {
            fw.write(String.format("%d %.1f %.1f %.1f\n", i + 1, vertices[i].x, vertices[i].y, vertices[i].z));
        }

        fw.write("Faces:\n");
        // Loop to define faces for each step
    for (int k = 0; k < n; k++) { // Step k
            int m = 8 * k;
            face(m, 1, 2, 6, 5);
            face(m, 4, 8, 7, 3);
            face(m, 5, 6, 7, 8);
            face(m, 1, 4, 3, 2);
            face(m, 2, 3, 7, 6);
            face(m, 1, 5, 8, 4);
        }
        fw.close();
    }

    void face(int m, int a, int b, int c, int d) throws IOException {
        // Adjusts the indices based on the base index m and writes the face definition to the file
        fw.write((m + a) + " " + (m + b) + " " + (m + c) + " " + (m + d) + ".\r\n");
    }
}
