INSERT INTO mail_templates (is_deleted, created_date, template_name, content, mail_subject, mail_from, mail_from_name)
SELECT
    false,
    NOW(),
    'EMAIL_CONFIRMATION',
    '<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ExtendRent Account Verification</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; text-align: center; }
        .container { max-width: 600px; margin: 20px auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        h2 { color: #333; }
        p { color: #555; }
        .verification-link { display: inline-block; padding: 10px 20px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Verify your account</h2>
        <p>Hello ${userName},</p>
        <p>Click the link below to complete your registration:</p>
        <a href="${confirmLink}" class="verification-link">Verify your account</a>
        <p>Or paste this link into your browser: ${confirmLink}</p>
        <p>If you did not make this request, please ignore this email.</p>
        <p>Thank you!</p>
    </div>
</body>
</html>',
    'Verify your email address',
    'noreply@extendrent.com',
    'ExtendRent'
WHERE NOT EXISTS (SELECT 1 FROM mail_templates WHERE template_name = 'EMAIL_CONFIRMATION');
