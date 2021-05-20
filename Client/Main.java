package com.company;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    static String ip;
    static int port;
    static String url_s;
    static String path = "C:\\Users\\Tom\\Desktop\\CSaN\\";

    public static void main(String[] args) {
        while (true) {
            getIP_port();
            url_s = "http://" + ip + ":" + String.valueOf(port) + "/";
            startTask();
        }
    }

    private static void startTask() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("Выберите действие:\n1-чтение файла\n2-перезапись файла\n3-добавление в конец файла\n" +
                    "4-удаление файла\n5-копирование файла\n6-перемещение файла\n0-exit");
            while (!scan.hasNextInt()) {
                System.out.println("Not a number");
            }
            int task = scan.nextInt();
            if (task < 0 || task > 6) {
                System.out.println("Choose from list");
                continue;
            }
            switch (task) {
                case 1: {
                    get();
                    break;
                }
                case 2: {
                    put();
                    break;
                }
                case 3: {
                    post();
                    break;
                }
                case 4: {
                    delete();
                    break;
                }
                case 5: {
                    copy();
                    break;
                }
                case 6: {
                    move();
                    break;
                }
                case 0: {
                    return;
                }
            }
        }
    }

    private static void getIP_port() {
        System.out.print("Введите IP: ");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ip = scanner.nextLine();
            if (ip.matches("[0-9]{1,3}([.][0-9]{1,3}){3}")) break;
            System.out.print("Введенное значение не соответсвует IP-адресу.\nВведите IP: ");
        }
        System.out.print("Введите номер порта: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Номер порта должен быть натуральным числом.\nВведите номер порта: ");
        }
        port = scanner.nextInt();
    }

    private static void get() {

        Scanner scan = new Scanner(System.in);
        System.out.println("Введите имя файла");
        String filename = scan.nextLine();
        try {
            URL url = new URL(url_s + filename);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            File saveFile = new File(path + filename);
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            fileOutputStream.write(response);
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println("Файл "+filename+" не найден");
        }
    }

    private static void put() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите имя файла");
        String filename = scan.nextLine();
        System.out.println("Введите новое содержимое файла");
        String cont = scan.nextLine();
        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .PUT(HttpRequest.BodyPublishers.ofString(URLEncoder.encode("content:"+cont)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("///"+URLEncoder.encode("content:"+cont));
            System.out.println(response);
        }catch (Exception e){
            System.out.println("Не получилось выполнить запрос");
        }
    }

    private static void post() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите имя файла");
        String filename = scan.nextLine();
        System.out.println("Введите, что необходимо добавить");
        String newPath = scan.nextLine();
        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(URLEncoder.encode("type:post&content:"+newPath)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("///"+URLEncoder.encode("content:"+newPath));
            System.out.println(response);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void delete() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите имя удаляемого файла");
        String filename = scan.nextLine();
        URI uri;
        try {
            uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copy() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите имя файла");
        String filename = scan.nextLine();
        System.out.println("Введите новый путь");
        String newPath = scan.nextLine();
        System.out.println("Введите новое имя файла");
        String newFilename = scan.nextLine();
        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(URLEncoder.encode("type:copy&newPath:"+newPath+"&newFilename:"+newFilename)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("///"+URLEncoder.encode("type:copy&newPath:"+newPath+"&newFilename:"+newFilename));
            System.out.println(response);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void move() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите имя файла");
        String filename = scan.nextLine();
        System.out.println("Введите новый путь");
        String newPath = scan.nextLine();
        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(URLEncoder.encode("type:move&newPath:"+newPath)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}