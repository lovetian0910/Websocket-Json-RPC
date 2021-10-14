package main

import (
	"context"
	"flag"
	"github.com/gorilla/websocket"
	"github.com/sourcegraph/jsonrpc2"
	websocketjsonrpc2 "github.com/sourcegraph/jsonrpc2/websocket"
	"log"
	"net/http"
)

var addr = flag.String("addr", ":8080", "http service address")

var upgrader = websocket.Upgrader{} // use default options
type JSONRPCHandle struct {

}
func (handle JSONRPCHandle) Handle(ctx context.Context, conn *jsonrpc2.Conn, req *jsonrpc2.Request) {
	log.Printf("method: %s, params: %s, isNotification: %t", req.Method, req.Params, req.Notif)
	//replyError := func() {
	//	_ = conn.ReplyWithError(ctx, req.ID, &jsonrpc2.Error{
	//		Code:    500,
	//		Message: fmt.Sprintf("Error"),
	//	})
	//}
	if !req.Notif {
		_ = conn.Reply(ctx, req.ID, "OK")
	}

}
func echo(w http.ResponseWriter, r *http.Request) {
	c, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Print("upgrade:", err)
		return
	}
	defer c.Close()
	jc := jsonrpc2.NewConn(r.Context(), websocketjsonrpc2.NewObjectStream(c), JSONRPCHandle{})
	<-jc.DisconnectNotify()
}

func main() {
	flag.Parse()
	log.SetFlags(0)
	http.HandleFunc("/echo", echo)
	log.Fatal(http.ListenAndServe(*addr, nil))
}
