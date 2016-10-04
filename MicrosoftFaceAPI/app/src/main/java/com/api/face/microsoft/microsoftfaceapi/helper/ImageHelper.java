package com.api.face.microsoft.microsoftfaceapi.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.api.face.microsoft.microsoftfaceapi.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alina_Zhdanava on 10/4/2016.
 */

public class ImageHelper {

    private Context context;
    private static FaceServiceClient faceServiceClient;

    public ImageHelper(Context context) {
        if(faceServiceClient == null) {
            this.context = context;
            faceServiceClient = new FaceServiceRestClient(context.getString(R.string.subscription_key));
        }
    }

    private static  ByteArrayInputStream convertBitmapToStream(Bitmap image)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,0, outputStream);
        byte[] bitmapdata = outputStream.toByteArray();
        return new ByteArrayInputStream(bitmapdata);
    }

    public static Face[] detect(Bitmap bitmap)
    {
        try {
          return   faceServiceClient.detect(
                    convertBitmapToStream(bitmap),
                    true,       /* Whether to return face ID */
                    true,       /* Whether to return face landmarks */
                                    /* Which face attributes to analyze, currently we support:
                                       age,gender,headPose,smile,facialHair */
                    new FaceServiceClient.FaceAttributeType[]{
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Glasses,
                            FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.HeadPose
                    });
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Face[0];
    }


    public static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }

    public static List<Person> identify(Face[] faces) throws IOException, ClientException {
        IdentifyResult[] identifyResults = faceServiceClient.identity("Strangers", getFacesId(faces), 1);
        List<Person> persons= new ArrayList<>();
        for (IdentifyResult result : identifyResults) {
            if (result.candidates.size() != 0) {
                UUID candidateId = result.candidates.get(0).personId;
                Person person = faceServiceClient.getPerson("Strangers",result.faceId);
                persons.add(person);
            }
        }
        return persons;
    }

    private static UUID[] getFacesId (Face[] faces)
    {
        UUID[] uuids =  new UUID[faces.length];
        for (int i = 0; i<faces.length ;i++) {
            uuids[i] = faces[i].faceId;
        }
        return  uuids;
    }


    public static void addPersonToTheGroup (String personGroupId ,Face[] faces , String personName ,String userData )
    {
        if(personGroupId == "")
            personGroupId = "Strangers";

        try {
            faceServiceClient.createPersonGroup(personGroupId, "Acquaintance","Acquaintance");
            CreatePersonResult man =   faceServiceClient.createPerson(personGroupId,personName,userData);
            faceServiceClient.addPersonFace(personGroupId,faces[0].faceId,"джи","джи",faces[0].faceRectangle);
            faceServiceClient.trainPersonGroup(personGroupId);
            TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(
                    personGroupId);     /* personGroupId */

            if (trainingStatus.status != TrainingStatus.Status.Succeeded) {

            }

            } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();}}

    }
