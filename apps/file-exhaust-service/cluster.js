/**
 * Setting up the cluster to the master and node, depending on the number of processor.
 */
const cluster = require('cluster');
// Getting the number of cpus available where the server is running
const numCPUs = require('os').cpus().length;

const log = require('./Logger/log');

// If the cluster is a master node, start the cluster with workers on the
if (process.argv[2] === 'start') {
	if (cluster.isMaster) {
		log.info('\n\nMaster ' + process.pid + ' is running\n\n');
		// This will come in soon after the content meta ports to the mongodb instead of elasticsearch
		// listener.startChangeListener();
		// metaLoader.startLoadAction();

		// Fork workers.
		for (var i = 0; i < numCPUs; i++) {
			// Starting the workers
			cluster.fork();
		}
		// cluster.fork();
		cluster.on('exit', (worker, code, signal) => {
			console.error('***********************\nWorker ' + worker.process.pid + ' died\n***********************'); //eslint-disable-line
			log.info('Signal is:', signal);
			// Starting a new worker
			cluster.fork();
		});
	} else {
		// Workers can share any TCP connection
		// In this case it is an HTTP server
		// Getting the server
		require('./app');
		log.info('Worker ' + process.pid + ' started');
	}
}
