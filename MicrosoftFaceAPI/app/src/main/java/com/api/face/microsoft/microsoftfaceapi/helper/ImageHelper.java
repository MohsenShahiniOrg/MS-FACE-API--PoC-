package com.api.face.microsoft.microsoftfaceapi.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import com.api.face.microsoft.microsoftfaceapi.R;
import com.bumptech.glide.Glide;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.PersonGroup;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.rest.ClientException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alina_Zhdanava on 10/4/2016.
 */

public class ImageHelper {

    private static Context context;
    private static FaceServiceClient faceServiceClient;

    public ImageHelper(Context context) {
        if (faceServiceClient == null) {
            this.context = context;
            faceServiceClient = new FaceServiceRestClient(context.getString(R.string.subscription_key));
        }
    }

    private static ByteArrayInputStream convertBitmapToStream(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        byte[] bitmapdata = outputStream.toByteArray();
        return new ByteArrayInputStream(bitmapdata);
    }

    public static void deleteAll() {
        PersonGroup[] groups = new PersonGroup[0];
        try {
            groups = faceServiceClient.getPersonGroups();
            for (PersonGroup group : groups) {
                faceServiceClient.deletePersonGroup(group.personGroupId);
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Face[] detect(Bitmap bitmap) {
        try {
            return faceServiceClient.detect(
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

    public static Face[] detectURL(String path) {
        try {
            return faceServiceClient.detect(
                    path,
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

    public static boolean createGroup(String personGroupId, String name) {
        try {
            if (personGroupId != null & name != null)
                faceServiceClient.createPersonGroup(personGroupId, name, "data");
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean createPerson(String personGroupId, String personName, String imagePath) {

        CreatePersonResult person = null;
        try {
            person = faceServiceClient.createPerson(personGroupId, personName, "data");

            Bitmap bitmap = Glide.with(context).load(imagePath).asBitmap().into(200, 200).get();
            Face[] faces = ImageHelper.detect(bitmap);
            InputStream bitmapStream = convertBitmapToStream(bitmap);
            faceServiceClient.addPersonFace(personGroupId, person.personId, bitmapStream, "usedData", faces[0].faceRectangle);

        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void trainGroup(String personGroupId) {
        try {
            faceServiceClient.trainPersonGroup(personGroupId);
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static TrainingStatus trainSatus(String personGroupId) {
        try {
            faceServiceClient.trainPersonGroup(personGroupId);
            return faceServiceClient.getPersonGroupTrainingStatus(personGroupId);

        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
return null;
    }


    public static ArrayList<PersonGroup> getAllGroups() {

        try {
            return new ArrayList<PersonGroup>(Arrays.asList(faceServiceClient.getPersonGroups()));
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static ArrayList<Person> getAllGroupPersons(String personGroupId) {
        try {
            return new ArrayList<Person>(Arrays.asList(faceServiceClient.getPersons(personGroupId)));
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String identify(String imagePath) {
        Bitmap bitmap = null;
        String identifyresult = null;
        try {

            bitmap = Glide.with(context).load(imagePath).asBitmap().into(200, 200).get();
            Face[] faces = ImageHelper.detect(bitmap);

            PersonGroup[] groups = faceServiceClient.getPersonGroups();

            for (PersonGroup group : groups) {
                TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(group.personGroupId);
                Log.i("TAG", trainingStatus.status + " status " + group.personGroupId);
                if (trainingStatus.status == TrainingStatus.Status.Succeeded) {

                    IdentifyResult[] result = faceServiceClient.identity(group.personGroupId, ImageHelper.getFacesId(faces), 1);
                    if (result[0].candidates.size() > 0) {
                        identifyresult += result[0].candidates.get(0).confidence + " -- " + faceServiceClient.getPerson(group.personGroupId, result[0].candidates.get(0).personId).name ;
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return identifyresult;
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


    public static UUID[] getFacesId(Face[] faces) {
        UUID[] uuids = new UUID[faces.length];
        for (int i = 0; i < faces.length; i++) {
            uuids[i] = faces[i].faceId;
        }
        return uuids;
    }

}
