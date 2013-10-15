using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SocketServer
{
    [Serializable]
    public class SocketResponse
    {
        public string Method { get; set; }

        public string DateTime { get; set; }

        public object Result { get; set; }
    }
}
