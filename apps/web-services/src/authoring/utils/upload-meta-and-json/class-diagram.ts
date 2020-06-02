import { IUploadPartialS3Response, IUploadS3Request } from '../../models/response/custom-s3-upload'
import { uploadToS3 } from '../S3/upload'
import { IClassDiagram } from './../../models/class-diagram'

export async function uploadClassdiagramData(
    data: IUploadS3Request<IClassDiagram>
): Promise<IUploadPartialS3Response> {
    const key = uploadToS3(data.data, data.path, 'class-diagram-key.json')
    const withoutKey = uploadToS3(removeKeyFromClassDiagram(data.data), data.path, 'class-diagram.json')
    const keyReport = await key
    const withoutKeyReport = await withoutKey
    if (keyReport.artifactUrl && withoutKeyReport.artifactUrl) {
        return withoutKeyReport
    } else {
        return {
            artifactUrl: null,
            downloadUrl: null,
            error: keyReport.error || withoutKeyReport.error,
        }
    }
}

function removeKeyFromClassDiagram(data: IClassDiagram): IClassDiagram {
    const returnValue: IClassDiagram = JSON.parse(JSON.stringify(data))
    returnValue.options.classes.forEach((classObj) => {
        classObj.belongsTo = ''
        classObj.type = ''
        classObj.access = ''
    })
    returnValue.options.relations = []
    return returnValue
}
