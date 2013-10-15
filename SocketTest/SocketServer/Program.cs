using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SocketServer
{
    class Program
    {
        static void Main(string[] args)
        {
            SocketHost host = new SocketHost { Port = 1234 };
            host.Start();

            Console.ReadLine();
        }
    }
}
