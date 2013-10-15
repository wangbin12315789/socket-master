using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;

namespace SocketServer
{
    public interface IResponseManager
    {
        void Write(Socket sender, IList<Socket> cliens, IDictionary<string, object> param);
    }
}
