import { instance } from "@viz-js/viz"

function mapContentWith(data, k) {
  if (data.GraphvizFigure) {
    let name = data.GraphvizFigure.title
    let source = data.GraphvizFigure.source
    instance().then(viz => {
      const graph = viz.renderSVGElement(source)
      let html = graph.outerHTML
      k({ name, html, multistep: false })
    })
  } else if (data.PlainText) {
    let name = data.PlainText.title
    let text = data.PlainText.text
    let html = `<pre>${text}</pre>`
    k({ name, html, multistep: false })
  } else if (data.ReductionSeq) {
    let name = data.ReductionSeq.title
    let steps0 = data.ReductionSeq.steps 
    instance().then(viz => {
      let steps1 = steps0.map(step => {
        let graph = viz.renderSVGElement(step[1])
        let res = { expr: step[0], graph: graph.outerHTML }
        return res
      })
      console.log("COMPLED STEPS", steps1)
      k({ name, steps: steps1, multistep: true })
    })
  } else {
    console.log("unrecognized data: ", data)
  }
}

var app = new Vue({
  el: "#app",
  data: {
    message: "Hello Vue!",
    graph: "",
    isError: false,
    errorMessage: "",
    contents: [],
  },
  methods: {
    onShowClick: function() {
      this.isError = false
      this.contents = []
      // get input from #expr
      const expr = document.getElementById("expr").value
      // get input from radio buttons of name exprtype
      const exprtype = document.querySelector('input[name="exprtype"]:checked').value
      console.log(expr)
      console.log(exprtype)

      // make a request to /calc/exprtype/expr and print the output
      // url encode first
      fetch(`/calc/${exprtype}/${encodeURIComponent(expr)}`)
        .then(response => response.json())
        .then(data => {
          console.log(data)
          if (data.Error) {
            this.isError = true
            this.errorMessage = data.Error.msg
          } else if (data.Ok) {
            let xs = data.Ok.contents
            xs.forEach(d => {
              // console.log("MAPPING")
              // console.log(d)
              var obj0 = { name: "...", html: "...", multistep: false }
              this.contents.push(obj0)
              mapContentWith(d, obj => {
                // console.log("MAPPED")
                // console.log(obj)
                obj0.name = obj.name
                obj0.multistep = obj.multistep
                if (!obj.multistep) obj0.html = obj.html
                else obj0.steps = obj.steps
                // console.log(this.contents)
              })
            })
          }
        })
        .catch(err => {
          console.log(err)
          this.isError = true
          this.errorMessage = err
        })

      instance().then(viz => {
        const graph = viz.renderSVGElement("digraph { a -> b }")
        this.graph = graph.outerHTML
      })
    }
  }
})