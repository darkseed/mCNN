import org.apache.spark.mllib.neuralNetwork.{CNNTopology, Scale, CNNLayer, CNN}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext, SparkConf}

object Driver {
  def main(args: Array[String]) {
    val topology = new CNNTopology
    topology.addLayer(CNNLayer.buildInputLayer(new Scale(28, 28)))
    topology.addLayer(CNNLayer.buildConvLayer(6, new Scale(5, 5)))
    topology.addLayer(CNNLayer.buildSampLayer(new Scale(2, 2)))
    topology.addLayer(CNNLayer.buildConvLayer(12, new Scale(5, 5)))
    topology.addLayer(CNNLayer.buildSampLayer(new Scale(2, 2)))
    topology.addLayer(CNNLayer.buildOutputLayer(10))
    val cnn: CNN = new CNN(topology).setMaxIterations(500000).setMiniBatchSize(16)

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)
    val conf = new SparkConf().setMaster("local[8]").setAppName("ttt")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("dataset/train.format", 8)
    val data = lines.map(line => line.split(",")).map(arr => arr.map(_.toDouble))
      .map(arr => new LabeledPoint(arr(784), Vectors.dense(arr.slice(0, 784))))

    val start = System.nanoTime()
    cnn.trainOneByOne(data)
    println("Training time: " + (System.nanoTime() - start) / 1e9)
  }

}
