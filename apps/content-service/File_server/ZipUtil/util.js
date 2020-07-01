/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
// Require for zipping the host util
const fs = require('fs');
const fse = require('fs-extra');
const archiver = require('archiver');
// const unzip = require('unzip');
const unzip = require('unzip-stream');
const log = require('../Logger/log');

function zipTheDirectory(sourcePath, destinationPath, callback) {
	// create a file to stream archive data to.
	const output = fs.createWriteStream(destinationPath);
	const archive = archiver('zip', {
		zlib: {
			level: 9
		} // Sets the compression level.
	});
	// const archive = archiver('zip', {});

	// listen for all archive data to be written
	// 'close' event is fired only when a file descriptor is involved
	output.on('close', function () {
		log.info(archive.pointer() + ' total bytes');
		log.info('archiver has been finalized and the output file descriptor has closed.');
		if (callback) {
			callback(null, {
				size: archive.pointer()
			});
		}
	});

	// This event is fired when the data source is drained no matter what was the data source.
	// It is not part of this library but rather from the NodeJS Stream API.
	// @see: https://nodejs.org/api/stream.html#stream_event_end
	output.on('end', function () {
		log.info('Data has been drained');
	});

	// good practice to catch warnings (ie stat failures and other non-blocking errors)
	archive.on('warning', function (err) {
		if (err.code === 'ENOENT') {
			// log warning
		} else {
			// throw error
			callback(err, null);
		}
	});

	// good practice to catch this error explicitly
	archive.on('error', function (err) {
		// throw err;
		callback(err, null);
	});

	// pipe archive data to the file
	archive.pipe(output);

	archive.directory(sourcePath, ''); // '' will zip the data as the data directory mentioned in the source.

	// finalize the archive (ie we are done appending files but streams have to finish yet)
	// 'close', 'end' or 'finish' may be fired right after calling this method so register to them beforehand
	archive.finalize();
}

function unzipTheDirectory(sourcePath, destinationPath) {
	try {
		fse.createReadStream(sourcePath).pipe(unzip.Extract({
			path: destinationPath
		}));
	} catch (ex) {
		console.error(ex); // eslint-disable-line
		throw ex;
	}
}

module.exports = {
	zipTheDirectory: zipTheDirectory,
	unzipTheDirectory: unzipTheDirectory
};
