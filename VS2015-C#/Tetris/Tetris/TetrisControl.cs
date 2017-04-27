using System;
using System.Collections.Generic;

namespace Tetris
{
    partial class Control
    {
        public enum Color
        {
            None,
            Green,
            Red,
            Yellow,
            Blue,
            Cyan,
            Orange,
            Purple
        }

        public struct Point
        {
            public int y, x;
            public Point(int y, int x) { this.x = x; this.y = y; }
            public static Point operator +(Point p1, Point p2)
            {
                return new Point(p1.y + p2.y, p1.x + p2.x);
            }
            public static implicit operator Point(int[] pos)
            {
                return new Point(pos[0], pos[1]);
            }
        }


        protected class Polymino
        {
            private Point Position;
            public int[,] Structure { get; protected set; }
            virtual protected bool Rotateable { get { return true; } }
            public Color Type { get; }
            protected Polymino(int[,] structure, Color type)
                : this(new Point(0, 0), structure, type)
            { }
            // Alternativ auch abstract Attribut zum Überladen anstelle des Constructors interessant
            protected Polymino(Point position, int[,] structure, Color type)
            {
                Position = position;
                Structure = structure;
                Type = type;
            }
            /*            public static Polymino operator +(Polymino poly, Point p)
                        {
                            return new Polymino(poly.Position + p, poly.Structure);
                        }
            */

            public void MoveTo(Point pt)
            {
                this.Position = pt;
            }
            public void Move(Point by)
            {
                this.Position += by;
            }

            public Point[] GetPositions()
            {
                List<Point> points = new List<Point>();
                for (int y = 0; y < Structure.GetLength(0); y++)
                    for (int x = 0; x < Structure.GetLength(1); x++)
                        if (Structure[y, x] != 0)
                            points.Add(new Point(y, x) + this.Position);
                return points.ToArray();
            }

            public void RotateRight()
            {
                if (!Rotateable)
                    return;

                int length = Structure.GetLength(1);

                int[,] res = new int[length, length];

                for (int y = 0; y < length; y++)
                    for (int x = 0; x < length; x++)
                        res[y, x] = Structure[length - x - 1, y];
                Structure = res;
            }

            public void RotateLeft()
            {
                if (!Rotateable)
                    return;

                int length = Structure.GetLength(0);

                int[,] res = new int[length, length];

                for (int y = 0; y < length; y++)
                    for (int x = 0; x < length; x++)
                        res[y, x] = Structure[x, length - y - 1];
                Structure = res;
            }
        }

        private class I4 : Polymino
        {
            public I4() : base(
                new int[,]{
                    { 0,0,0,0,0 },
                    { 0,0,0,0,0 },
                    { 1,1,1,1,0 },
                    { 0,0,0,0,0 },
                    { 0,0,0,0,0 }
                }, Color.Cyan)
            {
            }
        }

        private class O4 : Polymino
        {
            public O4() : base(
                new int[,]{
                    { 0,0,0,0,0 },
                    { 0,1,1,0,0 },
                    { 0,1,1,0,0 },
                    { 0,0,0,0,0 },
                    { 0,0,0,0,0 }
                }, Color.Yellow)
            { }
            protected override bool Rotateable { get { return false; } }
        }

        private class T4 : Polymino
        {
            public T4() : base(
                new int[,]{
                    { 0,0,0,0,0 },
                    { 0,0,0,0,0 },
                    { 0,1,1,1,0 },
                    { 0,0,1,0,0 },
                    { 0,0,0,0,0 }
                }, Color.Purple)
            { }
        }

        private class J4 : Polymino
        {
            public J4() : base(
                new int[,]{
                    { 0,0,0,0,0 },
                    { 0,0,0,0,0 },
                    { 0,1,1,1,0 },
                    { 0,0,0,1,0 },
                    { 0,0,0,0,0 }
                }, Color.Blue)
            { }
        }

        private class L4 : Polymino
        {
            public L4() : base(
                new int[,]{
                    { 0,0,0,0,0 },
                    { 0,0,0,0,0 },
                    { 0,1,1,1,0 },
                    { 0,1,0,0,0 },
                    { 0,0,0,0,0 }
                }, Color.Orange)
            { }
        }

        private class S4 : Polymino
        {
            public S4() : base(
                new int[,]{
                    { 0,0,0,0,0 },
                    { 0,0,1,0,0 },
                    { 0,1,1,0,0 },
                    { 0,1,0,0,0 },
                    { 0,0,0,0,0 }
                }, Color.Green)
            { }
        }

        private class Z4 : Polymino
        {
            public Z4() : base(
                new int[,]{
                    { 0,0,0,0,0 },
                    { 0,1,0,0,0 },
                    { 0,1,1,0,0 },
                    { 0,0,1,0,0 },
                    { 0,0,0,0,0 }
                }, Color.Red)
            { }
        }

        //
        private Color[,] grid;
        private static Random PRandom = new Random();
        public int Score { get; private set; } = 0;

        // List of available Tetriminos-specializations
        private Type[] Tetriminos = {
            typeof(I4),
            typeof(O4),
            typeof(T4),
            typeof(J4),
            typeof(L4),
            typeof(S4),
            typeof(Z4)
        };

        private Polymino Tetrimino = null;

        private void NewTetrimino()
        {
            // create new Random Tetrimino and move it in the top  center of the playground
            Tetrimino = (Polymino)Activator.CreateInstance(Tetriminos[PRandom.Next(0, Tetriminos.Length)]);
            Tetrimino.MoveTo(new Point(0, (grid.GetLength(1) / 2) - (Tetrimino.Structure.GetLength(1) / 2)));
        }

        // Tetrimino has valid positions
        private bool IsValid()
        {
            // no tetrimino is good tetrimino
            if (Tetrimino == null)
                return true;

            foreach (var pos in Tetrimino.GetPositions()) {
                // out of bounds
                if (pos.y > (grid.GetLength(0) - 1)
                        || (pos.x > (grid.GetLength(1) - 1))
                        || (pos.y < 0)
                        || (pos.x < 0))
                    return false;

                // already set tile
                if (grid[pos.y, pos.x] != Color.None)
                    return false;
            }
            return true;
        }
        
        // clean up lines and return count
        private int CleanBottom()
        {
            int cleaned = 0;
            // on clearing of line, move upper lines down one by one, by one; (quick and dirty)
            for (int y = 0; y < grid.GetLength(0); y++) {
                // check for full lines
                bool LineFull = true;
                for (int x = 0; x < grid.GetLength(1); x++) {
                    if (grid[y, x] == Color.None) {
                        LineFull = false;
                        break;
                    }
                }

                if (LineFull) {
                    ++cleaned;
                    // pull down lines before
                    for (int aboveY = y; aboveY > 0; aboveY--) {
                        for (int x = 0; x < grid.GetLength(1); x++)
                            grid[aboveY, x] = grid[aboveY - 1, x];
                    }
                    // clear out first line anyhow
//                    for (int x = 0; x < grid.GetLength(1); x++)
//                        grid[y, x] = Color.None;
                }
            }
            return cleaned;
        }
    }


    /************* Player Interface ****************/
    partial class Control
    {
        public Control(int height, int width)
        {
            grid = new Color[height, width];
        }

        public void Reset()
        {
            grid = new Color[grid.GetLength(0), grid.GetLength(1)];
            Score = 0;
        }
        public Color[,] PlayScreen()
        {
            Color[,] res = new Color[grid.GetLength(0), grid.GetLength(1)];

            for (int y = 0; y < grid.GetLength(0); y++)
                for (int x = 0; x < grid.GetLength(1); x++)
                    res[y, x] = grid[y, x];

            if (Tetrimino != null) {
                foreach (var pos in Tetrimino.GetPositions())
                    res[pos.y, pos.x] = Tetrimino.Type;
            }
            return res;
        }

        public bool RotateLeft()
        {
            if (Tetrimino != null) {
                Tetrimino.RotateLeft();
                if (!IsValid()) {
                    Tetrimino.RotateRight();
                    return false;
                }
            }
            return true;
        }

        public bool RotateRight()
        {
            if (Tetrimino != null) {
                Tetrimino.RotateRight();
                if (!IsValid()) {
                    Tetrimino.RotateLeft();
                    return false;
                }
            }
            return true;
        }


        public bool MoveLeft()
        {
            if (Tetrimino != null) {
                Tetrimino.Move(new Point(0, -1));
                if (!IsValid()) {
                    Tetrimino.Move(new Point(0, 1));
                    return false;
                }
            }
            return true;
        }
        public bool MoveRight()
        {
            if (Tetrimino != null) {
                Tetrimino.Move(new Point(0, 1));
                if (!IsValid()) {
                    Tetrimino.Move(new Point(0, -1));
                    return false;
                }
            }
            return true;
        }

        public bool Next()
        {
            if (Tetrimino == null) {
                NewTetrimino();
                if (!IsValid())
                    return false; // XXX
            }

            Tetrimino.Move(new Point(1, 0));
            if (IsValid() == false) {
                // Move back and place on ground
                Tetrimino.Move(new Point(-1, 0));
                foreach (var pos in Tetrimino.GetPositions())
                    grid[pos.y, pos.x] = Tetrimino.Type;
                Tetrimino = null;
                Score += CleanBottom();
            }
            return true;
        }

    }
}
