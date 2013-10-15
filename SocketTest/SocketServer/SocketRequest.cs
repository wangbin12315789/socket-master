using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SocketServer
{
    [Serializable]
    public class SocketRequest
    {
        public string Method { get; set; }

        public string DateTime { get; set; }

        public object Param { get; set; }
    }
}
