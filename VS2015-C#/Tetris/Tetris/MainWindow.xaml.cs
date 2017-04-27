using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Windows.Threading;

namespace Tetris
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        const int HEIGHT = 21;
        const int WIDTH = 10;
        const int PIXELSIZE = 30;

        const int TIMERINTERVAL = 175; // ms

        Control controller;
        static readonly object playLock = new object();
        Rectangle[,] graphgrid;
        DispatcherTimer timer;

        private void InitializeGrid(int height, int width, int pixelsize)
        {
            cnvGame.Width = width * pixelsize;
            cnvGame.Height = height * pixelsize;

            graphgrid = new Rectangle[height, width];

            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    Rectangle pixel = new Rectangle();
                    pixel.Width = pixel.Height = PIXELSIZE;
                    pixel.Fill = new SolidColorBrush(Colors.Beige);
                    Canvas.SetLeft(pixel, x * PIXELSIZE);
                    Canvas.SetTop(pixel, y * PIXELSIZE);
                    graphgrid[y, x] = pixel;
                    cnvGame.Children.Add(graphgrid[y,x]);
                }
            }
        }

        public MainWindow()
        {
            InitializeComponent();
            InitializeGrid(HEIGHT, WIDTH, PIXELSIZE);
            controller = new Tetris.Control(HEIGHT, WIDTH);
        }


        private void DrawGrid()
        {
            var grid = controller.PlayScreen();

            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    Rectangle pixel = graphgrid[y, x];
                    pixel.Width = pixel.Height = PIXELSIZE;
                    Color color;

                    switch (grid[y, x]) {
                        case Control.Color.None:
                            color = Colors.Beige;
                            break;
                        case Control.Color.Green:
                            color = Colors.Green;
                            break;
                        case Control.Color.Red:
                            color = Colors.Red;
                            break;
                        case Control.Color.Yellow:
                            color = Colors.Yellow;
                            break;
                        case Control.Color.Blue:
                            color = Colors.Blue;
                            break;
                        case Control.Color.Cyan:
                            color = Colors.Cyan;
                            break;
                        case Control.Color.Orange:
                            color = Colors.Orange;
                            break;
                        case Control.Color.Purple:
                            color = Colors.Purple;
                            break;
                        default:
                            color = Colors.Black;
                            break;
                    }

                    // only redraw if color is different
                    if (pixel != null && pixel.Fill != null && ((SolidColorBrush)pixel.Fill).Color != color) {
                        pixel.Fill = new SolidColorBrush(color);

                        if (grid[y, x] == Control.Color.None) {
                            pixel.Stroke = null;
                        } else {
                            Color darker = Color.Multiply(color, 0.6f);
                            pixel.Stroke = new SolidColorBrush(darker);
                            pixel.StrokeThickness = PIXELSIZE / 4;
                        }
                        Canvas.SetLeft(pixel, x * PIXELSIZE);
                        Canvas.SetTop(pixel, y * PIXELSIZE);
                        graphgrid[y, x] = pixel;
                    }
                }
//                cnvGame.UpdateLayout();
            }
        }

        private void nextMove(object obj, EventArgs e)
        {
            lock (playLock) {
                if (!controller.Next())
                    StopGame();
                DrawGrid();
                lblScore.Content = controller.Score * 1000;
            };
        }

        void NewGame()
        {
            if (timer != null)
                timer.Stop();

            lock (playLock) {
                controller.Reset();
                DrawGrid();
            }

            timer = new System.Windows.Threading.DispatcherTimer();
            timer.Tick += new EventHandler(nextMove);
            timer.Interval = new TimeSpan(0, 0, 0, 0, TIMERINTERVAL);
            timer.Start();
        }

        void StopGame()
        {
            if (timer != null) {
                timer.Stop();
            }
        }

        private void button_Click(object sender, RoutedEventArgs e)
        {
            NewGame();
        }

        private void Window_KeyDown(object sender, KeyEventArgs e)
        {
            switch (e.Key) {
                case Key.Y:
                    lock (playLock) {
                        controller.RotateLeft();
                        DrawGrid();
                    }
                    break;
                case Key.X:
                    lock (playLock) {
                        controller.RotateRight();
                        DrawGrid();
                    }
                    break;
                case Key.Left:
                    lock (playLock) {
                        controller.MoveLeft();
                        DrawGrid();
                    }
                    break;
                case Key.Right:
                    lock (playLock) {
                        controller.MoveRight();
                        DrawGrid();
                    }
                    break;
                case Key.Down:
                    nextMove(null, null);
                    break;
            }
        }
    }
}
